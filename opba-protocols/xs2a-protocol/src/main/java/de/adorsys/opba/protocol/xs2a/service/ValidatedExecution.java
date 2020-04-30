package de.adorsys.opba.protocol.xs2a.service;

import de.adorsys.opba.protocol.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.context.ContextMode;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class that provides generic functionality for Services that can be called in Validation({@link ContextMode#MOCK_REAL_CALLS}
 * execution mode and Real ASPSP API calls ({@link ContextMode#REAL_CALLS}.
 * @param <T> Context type
 */
@RequiredArgsConstructor
public abstract class ValidatedExecution<T extends BaseContext> implements JavaDelegate {

    /**
     * Entrypoint for Flowable BPMN to call the service.
     */
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

    /**
     * Used to update BPMN context before any execution.
     */
    protected void doPrepareContext(DelegateExecution execution, T context) {
    }

    /**
     * Validation function template for BPMN context. Should check (if necessary) that are parameters
     * required to call ASPSP are present in context.
     */
    protected void doValidate(DelegateExecution execution, T context) {
    }

    /**
     * Real ASPSP call function template.
     */
    protected abstract void doRealExecution(DelegateExecution execution, T context);

    /**
     * Mock ASPSP API function call template. Used within validation process to imitate ASPSP responses, so
     * that certain internal parameters can be provided to context as the API input.
     */
    protected void doMockedExecution(DelegateExecution execution, T context) {
    }

    /**
     * Is called as a finishing operation to i.e. validate context after method call.
     */
    protected void doAfterCall(DelegateExecution execution, T context) {
    }
}
