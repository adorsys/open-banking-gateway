package de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation;

import com.google.common.io.Resources;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JsonTemplateInterpolation {

    private final Pattern interpolationTarget = Pattern.compile("(\\$\\{(.+?)})");

    @SneakyThrows
    public String interpolate(String templateResourcePath, SandboxContext context) {
        String template = Resources.asByteSource(Resources.getResource(templateResourcePath)).asCharSource(StandardCharsets.UTF_8).read();

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

    private String parseExpression(String expression, SandboxContext context) {
        String prefix = "";
        if (expression.startsWith("_")) {
            prefix = "_";
            expression = expression.substring(1);
        }

        return prefix + doParse(expression, context);
    }

    private String doParse(String expression, SandboxContext context) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext parseContext = new StandardEvaluationContext(new SpelCtx<>(context));
        return parser.parseExpression(expression, new TemplateParserContext()).getValue(parseContext, String.class);
    }

    @Getter
    @RequiredArgsConstructor
    private class SpelCtx<T> {
        private final T context;
    }
}
