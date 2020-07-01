package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("authenticatedLostAuth")
public class AuthenticatedLostAuth extends TemplateBasedOperationHandler {

    public AuthenticatedLostAuth(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(SandboxContext context) {
        if (!context.isPinOk()) {
            return "response-templates/wrong-pin.json";
        }

        return "response-templates/wrong-tan.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.ANY;
    }
}
