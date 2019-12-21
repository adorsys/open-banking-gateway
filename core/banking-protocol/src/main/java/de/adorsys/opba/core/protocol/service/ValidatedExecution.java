package de.adorsys.opba.core.protocol.service;

import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
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

        doValidate(execution, context);
        if (MOCK_REAL_CALLS == context.getMode()) {
            doMockedExecution(execution, context);
        } else {
            doRealExecution(execution, context);
        }
        doAfterCall(execution, context);
    }

    protected void doValidate(DelegateExecution execution, T context) {
    }

    protected abstract void doRealExecution(DelegateExecution execution, T context);

    protected void doMockedExecution(DelegateExecution execution, T context) {
    }

    protected void doAfterCall(DelegateExecution execution, T context) {
    }
}
