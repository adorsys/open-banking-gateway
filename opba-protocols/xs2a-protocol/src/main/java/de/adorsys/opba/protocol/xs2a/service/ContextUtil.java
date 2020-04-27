package de.adorsys.opba.protocol.xs2a.service;

import com.google.common.net.UrlEscapers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.function.Consumer;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

/**
 * Utility class to work with Flowable BPMN engine process context.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ContextUtil {

    /**
     * Read context from current execution.
     */
    public <T> T getContext(DelegateExecution execution, Class<T> ctxType) {
        return execution.getVariable(CONTEXT, ctxType);
    }

    /**
     * Get and update context of current execution in single operation with retry support for optimistic exceptions.
     */
    public <T> void getAndUpdateContext(DelegateExecution execution, Consumer<T> contextUpdater) {
        @SuppressWarnings("unchecked")
        T ctx = (T) execution.getVariable(CONTEXT);
        contextUpdater.accept(ctx);
        execution.setVariable(CONTEXT, ctx);
    }

    /**
     * Allows to perform string interpolation like '/ais/#{ctx.getName}' using the process context.
     */
    @SuppressWarnings("unchecked")
    public <R, T> R evaluateSpelForCtx(String expression, DelegateExecution execution, T context) {
        return (R) evaluateSpelForCtx(expression, execution, context, Object.class);
    }

    /**
     * Allows to perform string interpolation like '/ais/#{ctx.getName}' using the process context of defined class.
     */
    public <R, T> R evaluateSpelForCtx(
            String expression, DelegateExecution execution, T context, Class<R> resultClass) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext parseContext = new StandardEvaluationContext(new SpelCtx<>(execution, context));
        return parser.parseExpression(expression, new TemplateParserContext()).getValue(parseContext, resultClass);
    }

    /**
     * Helper class for string interpolation that allows:
     * <ul>
     *     <li>to generate URL safe versions of values: {@link SpelCtx#urlSafe(String)}</li>
     * </ul>
     */
    @Getter
    @RequiredArgsConstructor
    private class SpelCtx<T> {

        private final DelegateExecution execution;
        private final T context;

        public String urlSafe(String original) {
            return UrlEscapers.urlPathSegmentEscaper().escape(original);
        }
    }
}
