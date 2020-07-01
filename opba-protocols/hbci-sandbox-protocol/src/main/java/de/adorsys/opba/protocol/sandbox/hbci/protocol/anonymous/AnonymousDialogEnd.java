package de.adorsys.opba.protocol.sandbox.hbci.protocol.anonymous;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("anonymousDialogEnd")
public class AnonymousDialogEnd extends TemplateBasedOperationHandler {

    public AnonymousDialogEnd(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(SandboxContext context) {
        return "response-templates/anonymous/dialog-end.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.DIALOG_END;
    }
}
