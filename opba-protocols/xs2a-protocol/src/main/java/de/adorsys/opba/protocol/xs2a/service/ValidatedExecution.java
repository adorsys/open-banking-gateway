package de.adorsys.opba.protocol.xs2a.service;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ContextMode;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public abstract class ValidatedExecution<T extends BaseContext> implements JavaDelegate {

    @Override
    @Transactional(noRollbackFor = BpmnError.class)
    public void execute(DelegateExecution execution) {
        @SuppressWarnings("unchecked")
        T context = (T) ContextUtil.getContext(execution, BaseContext.class);

        doPrepareContext(execution, context);
        doValidate(execution, context);
        if (ContextMode.MOCK_REAL_CALLS == context.getMode()) {
            doMockedExecution(execution, context);
        } else {
            doRealExecution(execution, context);
        }
        doAfterCall(execution, context);
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
}
