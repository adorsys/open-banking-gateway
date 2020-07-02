package de.adorsys.opba.protocol.bpmnshared.service.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import de.adorsys.opba.protocol.bpmnshared.GlobalConst;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.function.Consumer;

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
        return execution.getVariable(GlobalConst.CONTEXT, ctxType);
    }

    /**
     * Get and update context of current execution in single operation with retry support for optimistic exceptions.
     */
    public <T> void getAndUpdateContext(DelegateExecution execution, Consumer<T> contextUpdater) {
        @SuppressWarnings("unchecked")
        T ctx = (T) execution.getVariable(GlobalConst.CONTEXT);
        contextUpdater.accept(ctx);
        execution.setVariable(GlobalConst.CONTEXT, ctx);
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

    public URI buildAndExpandQueryParameters(String urlTemplate, BaseContext context, String redirectCode) {
        return UriComponentsBuilder.fromHttpUrl(urlTemplate)
                .buildAndExpand(
                        ImmutableMap.of(
                                "sessionId", context.getAuthorizationSessionIdIfOpened(),
                                "redirectCode", redirectCode
                        )
                ).toUri();
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
