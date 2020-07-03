package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.authorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("authorizedAuthLost")
public class AuthorizedAuthLost extends TemplateBasedOperationHandler {

    public AuthorizedAuthLost(JsonTemplateInterpolation interpolation) {
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
