package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.authorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("authorizedSynchronization")
public class AuthorizedSynchronization extends TemplateBasedOperationHandler {

    public AuthorizedSynchronization(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(HbciSandboxContext context) {
        return "response-templates/authorized/synch.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.SYNCH;
    }
}
