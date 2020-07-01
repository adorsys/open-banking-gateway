package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public abstract class TemplateBasedOperationHandler extends OperationHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JsonTemplateInterpolation interpolation;

    @Override
    protected SandboxContext doExecute(DelegateExecution execution, SandboxContext context) {
        String templatePathValue = getTemplatePathAndUpdateCtxIfNeeded(context);
        log.info("Applying response template {}", templatePathValue);
        String result = interpolation.interpolateToHbci(templatePathValue, context);
        context.setResponse(result);
        return context;
    }

    protected abstract String getTemplatePathAndUpdateCtxIfNeeded(SandboxContext context);
}
