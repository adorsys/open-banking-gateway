package de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.common.primitives.Longs;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Account;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Bank;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.parsing.ParsingUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonTemplateInterpolation {

    private final Pattern interpolationTarget = Pattern.compile("(\\$\\{(.+?)})");
    private final Pattern loopAccounts = Pattern.compile("(\\$\\{(.+getLoopAccount.+?)})");

    private final ObjectMapper mapper;

    @SneakyThrows
    public String interpolateToHbci(String templateResourcePath, SandboxContext context) {
        Map<String, String> interpolated = interpolate(templateResourcePath, context);
        String type = interpolated.remove("A_TYPE");
        log.info("Using (unwrapped) message type: {}", type);
        Message message = new Message(type, ParsingUtil.SYNTAX);
        Set<String> kontos6Injected = new HashSet<>();
        Set<String> pathsToPrefix = ImmutableSet.of("GVRes\\.KUmsZeitRes.*\\.booked");
        for (Map.Entry<String, String> target : interpolated.entrySet()) {
            injectKonto6IfNeeded(message, target.getKey(), interpolated, kontos6Injected);
            message.propagateValue(
                    message.getPath() + "." + target.getKey(),
                    pathsToPrefix.stream().anyMatch(it -> target.getKey().matches(it)) ? "B" + target.getValue(): target.getValue(),
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

    // kapott creates does not handle which element to create properly
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

    private Message encryptAndSignMessage(SandboxContext context, Message message) {
        Sig sig = new Sig();
        PinTanPassport passport = new PinTanPassport(
                "300",
                ImmutableMap.of(
                        "client.passport.country", "DE",
                        "client.passport.blz", context.getBank().getBlz(),
                        "client.passport.customerId", context.getUser().getLogin(),
                        "client.passport.userId", context.getUser().getLogin()
                ),
                new HBCICallbackConsole(),
                new HBCIProduct("1234", "300")
        );
        passport.setPIN("noref");
        passport.setSysId(context.getSysId());
        sig.signIt(message, passport);
        // Signing causes element duplication - so dropping duplicates
        Map<String, SyntaxElement> existingPaths = new HashMap<>();
        message.getChildContainers().stream().flatMap(it -> it.getElements().stream()).forEach(it -> recursivelyEnumeratePaths(it, existingPaths));
        // It is ok to ignore top elements (Multi elems) from removal - they do not seem to duplicate
        nonRecursivelyRemoveDuplicatePathsAndDestroyDirectParentOnDuplicate(message, existingPaths);
        message.validate();
        message.enumerateSegs(1, SyntaxElement.ALLOW_OVERWRITE);

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
                    pathsToPrefix.contains(target.getKey()) ? "B" + target.getValue(): target.getValue(),
                    true,
                    true
            );
        }
        result.propagateValue(result.getPath() + ".MsgHead.MsgRef.dialogid", context.getDialogId(), true, true);
        result.propagateValue(result.getPath() + ".MsgHead.MsgRef.msgnum",  message.getValueOfDE(message.getPath() + ".MsgHead.msgnum"), true, true);
        result.validate();
        result.enumerateSegs(1, SyntaxElement.ALLOW_OVERWRITE);
        result.autoSetMsgSize();
        return result;
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
            if (0 == totElems || current.toString(0).matches("HITAN:\\d+:\\d+'")) {
                iterator.remove();
            }
        }

    }

    @SneakyThrows
    public Map<String, String> interpolate(String templateResourcePath, SandboxContext context) {
        String templateToParse = Resources.asByteSource(Resources.getResource(templateResourcePath)).asCharSource(StandardCharsets.UTF_8).read();
        Map<String, String> template = mapper.readValue(templateToParse,  new TypeReference<Map<String, String>>() {});
        List<Entry> accountLoop = extractAndRemoveFromTemplateAccountLoopEntries(template);
        Map<String, String> result = new HashMap<>();

        CtxWrapper staticCtx = new CtxWrapper(0, context);
        for (Map.Entry<String, String> entry : template.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            key = interpolate(key, new CtxWrapper(0, staticCtx));
            value = interpolate(value, new CtxWrapper(0, staticCtx));
            result.put(key, value);
        }

        for (Entry entry : accountLoop) {
            for (int accPos = 0; accPos < staticCtx.getUser().getAccounts().size(); ++accPos) {
                String key = interpolate(entry.getKey(), new CtxWrapper(accPos, staticCtx));
                String value = interpolate(entry.getValue(), new CtxWrapper(accPos, staticCtx));
                result.put(key, value);
            }
        }

        return result;
    }

    private String interpolate(String template, CtxWrapper context) {
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

    private boolean isLoopAccountsExpression(String expression) {
        return loopAccounts.matcher(expression).find();
    }

    private String parseExpression(String expression, CtxWrapper context) {
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

    private String doParse(String expression, CtxWrapper context) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext parseContext = new StandardEvaluationContext(new SpelCtx(context));
        return parser.parseExpression(expression, new TemplateParserContext()).getValue(parseContext, String.class);
    }

    @Getter
    @RequiredArgsConstructor
    private static class SpelCtx {
        private final CtxWrapper ctx;
    }

    @RequiredArgsConstructor
    private static class CtxWrapper extends SandboxContext {

        private final int loopPos;

        @Delegate
        private final SandboxContext context;

        public AccountWithPosition getLoopAccount() {
            return new AccountWithPosition(getUser().getAccounts().get(loopPos), context.getBank(), loopPos + 1);
        }

        public AccountWithPosition getTransactionsAccount() {
            String accKey = getRequest().getData().keySet().stream()
                    .filter(it -> it.matches("GV\\.KUmsZeit\\d+\\.KTV\\.number"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No account number for transaction list provided in request"));
            String accId = getRequest().getData().get(accKey);
            Account acc = getUser().getAccounts().stream().filter(it -> it.getNumber().equals(accId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(String.format("No account %s available for current user %s", accId, getUser().getLogin())));
            return new AccountWithPosition(acc, context.getBank(), 1);
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
    }

    @Data
    @AllArgsConstructor
    private static class Entry {

        private String key;
        private String value;
    }
}
