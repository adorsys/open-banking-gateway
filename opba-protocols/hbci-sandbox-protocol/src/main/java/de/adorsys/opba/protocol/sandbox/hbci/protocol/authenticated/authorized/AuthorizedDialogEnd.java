package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.authorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("authorizedDialogEnd")
public class AuthorizedDialogEnd extends TemplateBasedOperationHandler {

    public AuthorizedDialogEnd(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(SandboxContext context) {
        return "response-templates/authorized/dialog-end.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.DIALOG_END;
    }
}
