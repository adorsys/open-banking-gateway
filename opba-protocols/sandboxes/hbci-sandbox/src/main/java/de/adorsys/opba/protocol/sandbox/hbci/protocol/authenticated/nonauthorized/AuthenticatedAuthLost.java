package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("authenticatedAuthLost")
public class AuthenticatedAuthLost extends TemplateBasedOperationHandler {

    public AuthenticatedAuthLost(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(HbciSandboxContext context) {
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
