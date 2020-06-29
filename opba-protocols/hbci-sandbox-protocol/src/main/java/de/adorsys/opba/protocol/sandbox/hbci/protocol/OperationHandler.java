package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.CONTEXT;


public abstract class OperationHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        SandboxContext context = (SandboxContext) execution.getVariable(CONTEXT);

        Operation handled = handledRequestType();
        if (Operation.ANY != handled && context.getRequestOperation() != handledRequestType()) {
            throw new IllegalStateException(String.format("Can't handle request: %s, expected is %s", context.getRequestOperation(), handledRequestType()));
        }

        SandboxContext updated = doExecute(execution, context);
        execution.setVariable(CONTEXT, updated);
    }

    protected abstract SandboxContext doExecute(DelegateExecution execution, SandboxContext context);
    protected abstract Operation handledRequestType();
}
