package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("authenticatedDialogInitTan2Step")
public class AuthenticatedDialogInitSca extends TemplateBasedOperationHandler {

    public AuthenticatedDialogInitSca(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String templatePath(SandboxContext context) {
        return "response-templates/authenticated/dialog-init-sca.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.DIALOG_INIT_SCA_TAN_2_STEP;
    }

    @Override
    protected boolean customHandling(SandboxContext context) {
        return canHandle(context);
    }

    public static boolean canHandle(SandboxContext context) {
        return "HKTAN".equals(context.getRequest().getData().get("TAN2Step6.SegHead.code"))
                && "HKIDN".equals(context.getRequest().getData().get("TAN2Step6.ordersegcode"))
                && "4".equals(context.getRequest().getData().get("TAN2Step6.process"));
    }
}
