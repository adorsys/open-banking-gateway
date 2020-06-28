package de.adorsys.opba.protocol.sandbox.hbci.protocol.anonymous;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("anonymousDialogInitNotSupported")
public class AnonymousDialogInitNotSupported extends TemplateBasedOperationHandler {

    public AnonymousDialogInitNotSupported(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String templatePath(SandboxContext context) {
        return "response-templates/anonymous/dialog-init-anon-not-supported.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.DIALOG_INIT_ANON;
    }
}
