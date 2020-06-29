package de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Account;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class JsonTemplateInterpolation {

    private final Pattern interpolationTarget = Pattern.compile("(\\$\\{(.+?)})");
    private final Pattern loopAccounts = Pattern.compile("(\\$\\{(.+getLoopAccount.+?)})");

    private final ObjectMapper mapper;

    @SneakyThrows
    public String interpolateToHbci(String templateResourcePath, SandboxContext context) {
        return "";
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
            for (int accPos = 0; accPos < staticCtx.getAccounts().size(); ++accPos) {
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
            return new AccountWithPosition(getAccounts().get(loopPos), loopPos + 1);
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
