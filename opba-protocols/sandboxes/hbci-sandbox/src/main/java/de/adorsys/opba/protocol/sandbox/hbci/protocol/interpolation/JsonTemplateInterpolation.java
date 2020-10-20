package de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.common.primitives.Longs;
import de.adorsys.multibanking.domain.PaymentStatus;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Account;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Bank;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Transaction;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.User;
import de.adorsys.opba.protocol.sandbox.hbci.domain.HbciSandboxPayment;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.parsing.ParsingUtil;
import de.adorsys.opba.protocol.sandbox.hbci.repository.HbciSandboxPaymentRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.jetbrains.annotations.NotNull;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIProduct;
import org.kapott.hbci.passport.PinTanPassport;
import org.kapott.hbci.protocol.Message;
import org.kapott.hbci.protocol.MultipleSyntaxElements;
import org.kapott.hbci.protocol.SyntaxElement;
import org.kapott.hbci.security.Crypt;
import org.kapott.hbci.security.Sig;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonTemplateInterpolation {

    private final Pattern interpolationTarget = Pattern.compile("(\\$\\{(.+?)})");
    private final Pattern loopAccounts = Pattern.compile("(\\$\\{(.+getLoopAccount.+?)})");
    private final Pattern loopSca = Pattern.compile("(\\$\\{(.+getLoopScaMethod.+?)})");
    private final Pattern mt940LoopTransactions = Pattern.compile("(\\$\\{mt940Begin}.+?\\$\\{mt940End})", Pattern.DOTALL);

    private final ObjectMapper mapper;
    private final HbciSandboxPaymentRepository paymentRepository;

    @SneakyThrows
    public String interpolateToHbci(String templateResourcePath, HbciSandboxContext context) {
        Map<String, String> interpolated = interpolate(templateResourcePath, context);
        String type = interpolated.remove("A_TYPE");
        log.info("Using (unwrapped) message type: {}", type);
        Message message = new Message(type, ParsingUtil.SYNTAX);
        Set<String> kontos6Injected = new HashSet<>();
        Set<String> pathsToPrefix = ImmutableSet.of("GVRes\\.KUmsZeitRes.*\\.booked", "GVRes\\.InstantUebSEPAStatusRes.*\\.sepapain");
        for (Map.Entry<String, String> target : interpolated.entrySet()) {
            injectKonto6IfNeeded(message, target.getKey(), interpolated, kontos6Injected);
            message.propagateValue(
                    message.getPath() + "." + target.getKey(),
                    pathsToPrefix.stream().anyMatch(it -> target.getKey().matches(it)) ? "B" + target.getValue() : target.getValue(),
                    true,
                    true
            );
        }

        if (context.isCryptNeeded()) {
            log.info("Encryption needed for {} of {}", templateResourcePath, context.getDialogId());
            message = encryptAndSignMessage(context, message);
            return message.toString(0);
        }

        message.validate();
        message.enumerateSegs(1, SyntaxElement.ALLOW_OVERWRITE);
        message.autoSetMsgSize();
        return message.toString(0);
    }

    // kapott creates does not handle which element to create properly if 2 entries have same name like KInfo5 and KInfo6
    // It simply creates 1st one (KInfo5) that does not have IBAN
    @SneakyThrows
    private void injectKonto6IfNeeded(Message message, String key, Map<String, String> values, Set<String> kontos6Injected) {
        Pattern konto6Pattern = Pattern.compile("UPD\\.KInfo.*\\.iban");
        Pattern targetPattern = Pattern.compile("(UPD\\.KInfo.*?\\.)");
        Pattern kontoPattern = Pattern.compile("UPD\\.KInfo.*");

        if (!kontoPattern.matcher(key).find()) {
            return;
        }

        Matcher matcher = targetPattern.matcher(key);
        matcher.find();
        String root = matcher.group(1);
        boolean hasKonto6 = values.entrySet().stream()
                .filter(it -> it.getKey().startsWith(root))
                .anyMatch(it -> konto6Pattern.matcher(it.getKey()).matches());

        if (!hasKonto6 || kontos6Injected.contains(root)) {
            return;
        }

        log.info("Injecting Konto6 for {}", key);
        kontos6Injected.add(root);
        SyntaxElement updElem = message.getElement(message.getPath() + ".UPD");
        XPath xPath = XPathFactory.newInstance().newXPath();
        Node konto6 = (Node) xPath.compile("/hbci/SFs/SFdef[@id='UPD']/SEG[@type='KInfo6']").evaluate(ParsingUtil.SYNTAX, XPathConstants.NODE);
        int konto6Idx = ((Double) xPath
                .compile("count(/hbci/SFs/SFdef[@id='UPD']/SEG[@type='KInfo6']/preceding-sibling::*)+1")
                .evaluate(ParsingUtil.SYNTAX, XPathConstants.NUMBER)).intValue();

        Method createNewChildContainer = SyntaxElement.class.getDeclaredMethod("createNewChildContainer", Node.class, Document.class);
        createNewChildContainer.setAccessible(true);
        MultipleSyntaxElements newKonto6Elem = (MultipleSyntaxElements) createNewChildContainer.invoke(updElem, konto6, ParsingUtil.SYNTAX);
        // Ensure correct element position
        Method setIdx = MultipleSyntaxElements.class.getDeclaredMethod("setSyntaxIdx", int.class);
        setIdx.setAccessible(true);
        setIdx.invoke(newKonto6Elem, konto6Idx);

        updElem.getChildContainers().add(newKonto6Elem);
    }

    private Message encryptAndSignMessage(HbciSandboxContext context, Message message) {
        String userLogin = null == context.getUser() ? "noref" : context.getUser().getLogin();
        PinTanPassport passport = new PinTanPassport(
                "300",
                ImmutableMap.of(
                        "client.passport.country", "DE",
                        "client.passport.blz", context.getBank().getBlz(),
                        "client.passport.customerId", userLogin,
                        "client.passport.userId", userLogin
                ),
                new HBCICallbackConsole(),
                new HBCIProduct("1234", "300")
        );
        passport.setPIN("noref");
        passport.setSysId(context.getSysId());
        signMessage(message, passport);
        return encryptMessage(context, message, passport);
    }

    @NotNull
    private Message encryptMessage(HbciSandboxContext context, Message message, PinTanPassport passport) {
        // Crypt the message
        Crypt crypt = new Crypt(passport);
        message = crypt.cryptIt(message);

        // Converting from client message to institution message
        Set<String> pathsToPrefix = ImmutableSet.of("CryptData.data", "CryptHead.CryptAlg.enckey");
        // MsgRef also is needed - seem to get lost and is irrecoverable
        Message result = new Message("CryptedRes", ParsingUtil.SYNTAX);
        for (Map.Entry<String, String> target : message.getData().entrySet()) {
            result.propagateValue(
                    result.getPath() + "." + target.getKey(),
                    pathsToPrefix.contains(target.getKey()) ? "B" + target.getValue() : target.getValue(),
                    true,
                    true
            );
        }
        result.propagateValue(result.getPath() + ".MsgHead.MsgRef.dialogid", context.getDialogId(), true, true);
        result.propagateValue(result.getPath() + ".MsgHead.MsgRef.msgnum", message.getValueOfDE(message.getPath() + ".MsgHead.msgnum"), true, true);
        result.validate();
        result.enumerateSegs(1, SyntaxElement.ALLOW_OVERWRITE);
        result.autoSetMsgSize();
        return result;
    }

    private void signMessage(Message message, PinTanPassport passport) {
        Sig sig = new Sig();
        sig.signIt(message, passport);
        // Signing causes element duplication - so dropping duplicates
        Map<String, SyntaxElement> existingPaths = new HashMap<>();
        message.getChildContainers().stream().flatMap(it -> it.getElements().stream()).forEach(it -> recursivelyEnumeratePaths(it, existingPaths));
        // It is ok to ignore top elements (Multi elems) from removal - they do not seem to duplicate
        nonRecursivelyRemoveDuplicatePathsAndDestroyDirectParentOnDuplicate(message, existingPaths);
        message.validate();
        message.enumerateSegs(1, SyntaxElement.ALLOW_OVERWRITE);
    }

    private void recursivelyEnumeratePaths(SyntaxElement element, Map<String, SyntaxElement> existingPaths) {
        existingPaths.putIfAbsent(element.getPath(), element);
        element.getChildContainers().stream().flatMap(it -> it.getElements().stream()).forEach(it -> recursivelyEnumeratePaths(it, existingPaths));
    }

    private void nonRecursivelyRemoveDuplicatePathsAndDestroyDirectParentOnDuplicate(SyntaxElement element, Map<String, SyntaxElement> existingPaths) {
        List<MultipleSyntaxElements> children = element.getChildContainers();
        Iterator<MultipleSyntaxElements> iterator = children.iterator();
        while (iterator.hasNext()) {
            MultipleSyntaxElements current = iterator.next();
            current.getElements().removeIf(elem -> !existingPaths.get(elem.getPath()).equals(elem) || elem.toString(0).matches("HITAN:\\d+:\\d+'"));
            int totElems = current.getElements().stream().mapToInt(it -> it.getChildContainers().size()).sum();
            if (0 == totElems || current.toString(0).matches("HITAN:\\d+:\\d+'") || current.toString(0).matches("HISYN:\\d+:\\d+'")) {
                iterator.remove();
            }
        }
    }

    @SneakyThrows
    public Map<String, String> interpolate(String templateResourcePath, HbciSandboxContext context) {
        String templateToParse = Resources.asByteSource(Resources.getResource(templateResourcePath)).asCharSource(StandardCharsets.UTF_8).read();
        Map<String, String> template = mapper.readValue(templateToParse, new TypeReference<Map<String, String>>() {
        });
        List<Entry> mt940TransactionLoop = extractAndRemoveFromTemplateTransactionLoopMt940Entries(template);
        List<Entry> accountLoop = extractAndRemoveFromTemplateAccountLoopEntries(template);
        List<Entry> scaLoop = extractAndRemoveFromTemplateScaLoopEntries(template);
        Map<String, String> result = new HashMap<>();

        AccountsContext staticCtx = new AccountsContext(0, context);
        for (Map.Entry<String, String> entry : template.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            key = doInterpolate(key, new AccountsContext(0, staticCtx));
            value = doInterpolate(value, new AccountsContext(0, staticCtx));
            result.put(key, value);
        }

        for (Entry entry : scaLoop) {
            for (int scaPos = 0; scaPos < staticCtx.getUser().getScaMethodsAvailable().size(); ++scaPos) {
                ScaContext accs = new ScaContext(scaPos, staticCtx);
                String key = doInterpolate(entry.getKey(), accs);
                String value = doInterpolate(entry.getValue(), accs);
                result.put(key, value);
            }
        }

        for (Entry entry : accountLoop) {
            for (int accPos = 0; accPos < staticCtx.getUser().getAccounts().size(); ++accPos) {
                AccountsContext accs = new AccountsContext(accPos, staticCtx);
                String key = doInterpolate(entry.getKey(), accs);
                String value = doInterpolate(entry.getValue(), accs);
                result.put(key, value);
            }
        }

        interpolateMT940TransactionsIfNeeded(mt940TransactionLoop, result, staticCtx);
        return result;
    }

    private void interpolateMT940TransactionsIfNeeded(List<Entry> mt940TransactionLoop, Map<String, String> result, AccountsContext staticCtx) {
        for (Entry entry : mt940TransactionLoop) {
            int accPos = getAccountPosForTransactions(staticCtx.getUser(), staticCtx);
            Account acc = staticCtx.getUser().getAccounts().get(accPos);
            List<Transaction> transactions = staticCtx.getUser().getTransactions().stream()
                    .filter(it -> it.getFrom().contains(acc.getNumber()))
                    .collect(Collectors.toList());

            processPaymentsThatAreTransactionsNow(staticCtx, acc, transactions);

            // Empty transaction list special handling
            if (transactions.isEmpty()) {
                TransactionsContext txns = new TransactionsContext(0, staticCtx, accPos, 0, transactions);
                result.put(entry.getKey(), interpolateTransactions(":20:STARTUMS\n:21:NONREF\n:25:${ctx.getBank().getBlz()}/${ctx.getTransactionsAccount().getNumber()}", txns));
                continue;
            }

            String initialValue = entry.getValue();
            StringBuilder value = new StringBuilder();
            for (int txnPos = 0; txnPos < transactions.size(); ++txnPos) {
                TransactionsContext txns = new TransactionsContext(0, staticCtx, accPos, txnPos, transactions);
                value.append(interpolateTransactions(initialValue, txns));
            }
            result.put(entry.getKey(), value.toString());
        }
    }

    private void processPaymentsThatAreTransactionsNow(AccountsContext staticCtx, Account acc, List<Transaction> transactions) {
        BigDecimal balance = acc.getBalance();
        List<HbciSandboxPayment> payments = paymentRepository.findByOwnerLoginAndStatusInOrderByCreatedAtDesc(
                staticCtx.getUser().getLogin(),
                ImmutableSet.of(PaymentStatus.ACSC) // only 'done' payments into transactions
        ).stream() // As it is sandbox we don't expect many transactions present, so filtering in code
                .filter(it -> it.getDeduceFrom().endsWith(acc.getNumber()) || it.getSendTo().endsWith(acc.getNumber()))
                .collect(Collectors.toList());
        for (HbciSandboxPayment payment : payments) {
            Transaction transaction = payment.toTransaction(acc.getNumber(), balance);
            balance = new BigDecimal(transaction.getBalanceAfter());
            transactions.add(transaction);
        }
    }

    private String interpolateTransactions(String template, AccountsContext context) {
        return doInterpolate(template.replaceAll("\\$\\{mt940Begin}", "").replaceAll("\\$\\{mt940End}", ""), context);
    }

    private String doInterpolate(String template, HbciSandboxContext context) {
        Matcher target = interpolationTarget.matcher(template);
        StringBuffer result = new StringBuffer();
        while (target.find()) {
            String expression = target.group(2);
            String parsed = parseExpression(expression, context);
            target.appendReplacement(result, parsed);
        }
        target.appendTail(result);

        return result.toString();
    }

    private List<Entry> extractAndRemoveFromTemplateAccountLoopEntries(Map<String, String> template) {
        List<Entry> accountLoop = new ArrayList<>();
        for (Map.Entry<String, String> entry : template.entrySet()) {
            if (!isLoopAccountsExpression(entry.getKey()) && !isLoopAccountsExpression(entry.getValue())) {
                continue;
            }

            accountLoop.add(new Entry(entry.getKey(), entry.getValue()));
        }

        accountLoop.forEach(it -> template.remove(it.getKey()));
        return accountLoop;
    }

    private List<Entry> extractAndRemoveFromTemplateScaLoopEntries(Map<String, String> template) {
        List<Entry> scaLoop = new ArrayList<>();
        for (Map.Entry<String, String> entry : template.entrySet()) {
            if (!isLoopScaExpression(entry.getKey())) {
                continue;
            }

            scaLoop.add(new Entry(entry.getKey(), entry.getValue()));
        }

        scaLoop.forEach(it -> template.remove(it.getKey()));
        return scaLoop;
    }

    private boolean isLoopAccountsExpression(String expression) {
        return loopAccounts.matcher(expression).find();
    }

    private boolean isLoopScaExpression(String expression) {
        return loopSca.matcher(expression).find();
    }

    private List<Entry> extractAndRemoveFromTemplateTransactionLoopMt940Entries(Map<String, String> template) {
        List<Entry> transactionLoop = new ArrayList<>();
        for (Map.Entry<String, String> entry : template.entrySet()) {
            if (!isLoopTransactionsMT940Expression(entry.getValue())) {
                continue;
            }

            transactionLoop.add(new Entry(entry.getKey(), entry.getValue()));
        }

        transactionLoop.forEach(it -> template.remove(it.getKey()));
        return transactionLoop;
    }

    private boolean isLoopTransactionsMT940Expression(String expression) {
        return mt940LoopTransactions.matcher(expression).find();
    }

    private String parseExpression(String expression, HbciSandboxContext context) {
        String prefix = "";
        if (expression.startsWith("_")) {
            prefix = "_";
            expression = expression.substring(1);
        }

        String result = doParse("#{" + expression + "}", context);

        // swallow the result if we need prefix and if returned value equals to numeric '1' -> _1 == ""
        if (null != result) {
            Long asInt = Longs.tryParse(result);
            if (!"".equals(prefix) && null != asInt && 1 == asInt) {
                return "";
            }
        }

        return prefix + result;
    }

    private String doParse(String expression, HbciSandboxContext context) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext parseContext = new StandardEvaluationContext(new SpelCtx(context));
        return parser.parseExpression(expression, new TemplateParserContext()).getValue(parseContext, String.class);
    }

    private static int getAccountPosForTransactions(User user, HbciSandboxContext context) {
        String accNumber = context.getAccountNumberRequestedBeforeSca();
        if (null == accNumber) {
            String accKey = context.getRequest().getData().keySet().stream()
                    .filter(it -> it.matches("GV\\.KUmsZeit\\d+\\.KTV\\.number"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No account number for transaction list provided in request"));
            accNumber = context.getRequest().getData().get(accKey);
        }

        for (int pos = 0; pos < user.getAccounts().size(); ++pos) {
            if (user.getAccounts().get(pos).getNumber().equals(accNumber)) {
                return pos;
            }
        }

        throw new IllegalStateException(String.format("No account %s available for current user %s", accNumber, user.getLogin()));
    }

    @Getter
    @RequiredArgsConstructor
    private static class SpelCtx {
        private final HbciSandboxContext ctx;
    }

    @RequiredArgsConstructor
    private static class AccountsContext extends HbciSandboxContext {

        private final int accountLoopPos;

        @Delegate
        protected final HbciSandboxContext context;

        public AccountWithPosition getLoopAccount() {
            return new AccountWithPosition(getUser().getAccounts().get(accountLoopPos), context.getBank(), accountLoopPos + 1);
        }
    }

    @RequiredArgsConstructor
    private static class ScaContext extends HbciSandboxContext {

        private final int scaLoopPos;

        @Delegate
        protected final HbciSandboxContext context;

        public ScaWithPosition getLoopScaMethod() {
            return new ScaWithPosition(scaLoopPos + 1, context.getUser().getScaMethodsAvailable().get(scaLoopPos));
        }
    }

    private static class TransactionsContext extends AccountsContext {

        private final int transactionAccPos;
        private final int transactionLoopPos;
        private final List<Transaction> transactions;

        TransactionsContext(int accountLoopPos, HbciSandboxContext context, int transactionAccPos, int transactionLoopPos, List<Transaction> transactions) {
            super(accountLoopPos, context);
            this.transactionAccPos = transactionAccPos;
            this.transactionLoopPos = transactionLoopPos;
            this.transactions = transactions;
        }

        public AccountWithPosition getTransactionsAccount() {
            return new AccountWithPosition(getUser().getAccounts().get(transactionAccPos), context.getBank(), transactionAccPos);
        }

        public Transaction getCurrentTransaction() {
            return transactions.get(transactionLoopPos);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class AccountWithPosition extends Account {

        @Delegate
        private final Account account;
        private final Bank bank;
        private final int position;

        public String getIban() {
            return new Iban.Builder()
                    .countryCode(CountryCode.valueOf(bank.getCountryCode()))
                    .bankCode(bank.getBlz())
                    .accountNumber(account.getNumber()).build()
                    .toString();
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class ScaWithPosition {

        private final int position;
        private final String id;
    }

    @Data
    @AllArgsConstructor
    private static class Entry {

        private String key;
        private String value;
    }
}
