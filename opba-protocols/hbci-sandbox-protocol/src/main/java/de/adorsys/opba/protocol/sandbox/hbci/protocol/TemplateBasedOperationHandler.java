package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;

@RequiredArgsConstructor
public abstract class TemplateBasedOperationHandler extends OperationHandler {

    private final JsonTemplateInterpolation interpolation;

    @Override
    protected SandboxContext doExecute(DelegateExecution execution, SandboxContext context) {
        String result = interpolation.interpolateToHbci(templatePath(context), context);
        context.setResponse(result);
        return context;
    }

    protected abstract String templatePath(SandboxContext context);
}
