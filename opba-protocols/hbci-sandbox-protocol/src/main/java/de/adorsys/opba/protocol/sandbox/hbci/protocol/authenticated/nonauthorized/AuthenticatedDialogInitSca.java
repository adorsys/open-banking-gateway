package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.MapRegexUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.RequestStatusUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("authenticatedDialogInitTan2Step")
public class AuthenticatedDialogInitSca extends TemplateBasedOperationHandler {

    public AuthenticatedDialogInitSca(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(SandboxContext context) {
        return "response-templates/authenticated/dialog-init-sca.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.DIALOG_INIT_SCA_TAN_2_STEP;
    }

    @Override
    protected boolean usesCustomHandlingAndHandles(SandboxContext context) {
        return canHandle(context.getRequest().getData());
    }

    public static boolean canHandle(Map<String, String> requestData) {
        return "HKTAN".equals(MapRegexUtil.getDataRegex(requestData, "TAN2Step\\d*\\.SegHead.code"))
                && "HKIDN".equals(MapRegexUtil.getDataRegex(requestData, "TAN2Step\\d*\\.ordersegcode"))
                && "4".equals(MapRegexUtil.getDataRegex(requestData, "TAN2Step\\d*\\.process"))
                && RequestStatusUtil.isForTransactionListing(requestData);
    }
}
