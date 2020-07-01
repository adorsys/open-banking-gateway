package de.adorsys.opba.protocol.sandbox.hbci.protocol.anonymous;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("anonymousDialogInitBpd")
public class AnonymousDialogInitBpd extends TemplateBasedOperationHandler {

    public AnonymousDialogInitBpd(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(SandboxContext context) {
        return "response-templates/anonymous/dialog-init-bpd.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.DIALOG_INIT_ANON;
    }
}
