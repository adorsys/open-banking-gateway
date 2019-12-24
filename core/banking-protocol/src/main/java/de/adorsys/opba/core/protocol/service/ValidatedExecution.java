package de.adorsys.opba.core.protocol.service;

import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.core.protocol.service.xs2a.context.ContextMode.MOCK_REAL_CALLS;

@RequiredArgsConstructor
public abstract class ValidatedExecution<T extends BaseContext> implements JavaDelegate {

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        @SuppressWarnings("unchecked")
        T context = (T) execution.getVariable(CONTEXT, BaseContext.class);

        doPrepareContext(execution, context);
        doValidate(execution, context);
        if (MOCK_REAL_CALLS == context.getMode()) {
            doMockedExecution(execution, context);
        } else {
            doRealExecution(execution, context);
        }
        doAfterCall(execution, context);
    }

    @SuppressWarnings("unchecked")
    protected <R> R evaluateSpelForCtx(String expression, DelegateExecution execution, T context) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext parseContext = new StandardEvaluationContext(new SpelCtx<>(execution, context));
        return (R) parser.parseExpression(expression, new TemplateParserContext()).getValue(parseContext);
    }

    protected void doPrepareContext(DelegateExecution execution, T context) {
    }

    protected void doValidate(DelegateExecution execution, T context) {
    }

    protected abstract void doRealExecution(DelegateExecution execution, T context);

    protected void doMockedExecution(DelegateExecution execution, T context) {
    }

    protected void doAfterCall(DelegateExecution execution, T context) {
    }

    @Getter
    @RequiredArgsConstructor
    private static class SpelCtx<T> {

        private final DelegateExecution execution;
        private final T context;
    }
}
