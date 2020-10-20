package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.CONTEXT;


public abstract class OperationHandler implements JavaDelegate {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(DelegateExecution execution) {
        HbciSandboxContext context = (HbciSandboxContext) execution.getVariable(CONTEXT);

        Operation handled = handledRequestType();
        log.info("Handling request of type {} with expected type {}", context.getRequestOperation(), handled);
        if (!usesCustomHandlingAndHandles(context) && Operation.ANY != handled && context.getRequestOperation() != handledRequestType()) {
            throw new IllegalStateException(String.format("Can't handle request: %s, expected is %s", context.getRequestOperation(), handledRequestType()));
        }

        HbciSandboxContext updated = doExecute(execution, context);
        execution.setVariable(CONTEXT, updated);
    }

    protected abstract HbciSandboxContext doExecute(DelegateExecution execution, HbciSandboxContext context);

    protected abstract Operation handledRequestType();

    protected boolean usesCustomHandlingAndHandles(HbciSandboxContext context) {
        return false;
    }
}
