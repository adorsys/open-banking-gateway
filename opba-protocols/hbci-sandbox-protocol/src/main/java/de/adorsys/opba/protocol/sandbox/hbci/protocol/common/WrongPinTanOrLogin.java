package de.adorsys.opba.protocol.sandbox.hbci.protocol.common;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("wrongPinTanOrLogin")
public class WrongPinTanOrLogin extends TemplateBasedOperationHandler {

    public WrongPinTanOrLogin(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(HbciSandboxContext context) {
        if (!context.isPinOk() || !context.getBank().getUsers().contains(context.getUser().getLogin())) {
            return "response-templates/wrong-pin.json";
        }

        return "response-templates/wrong-tan.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.ANY;
    }
}
