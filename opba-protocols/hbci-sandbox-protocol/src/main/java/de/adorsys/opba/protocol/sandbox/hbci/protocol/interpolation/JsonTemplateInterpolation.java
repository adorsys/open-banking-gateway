package de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Account;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.parsing.ParsingUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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
        Message message = new Message(type, ParsingUtil.SYNTAX);
        for (Map.Entry<String, String> target : interpolated.entrySet()) {
            message.propagateValue(message.getPath() + "." + target.getKey(), target.getValue(), true, true);
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

        return prefix + doParse("#{" + expression + "}", context);
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
            return new AccountWithPosition(getUser().getAccounts().get(loopPos), loopPos + 1);
        }

        @Getter
        @RequiredArgsConstructor
        private static class AccountWithPosition extends Account {

            @Delegate
            private final Account account;
            private final int position;
        }
    }

    @Data
    @AllArgsConstructor
    private static class Entry {

        private String key;
        private String value;
    }
}
