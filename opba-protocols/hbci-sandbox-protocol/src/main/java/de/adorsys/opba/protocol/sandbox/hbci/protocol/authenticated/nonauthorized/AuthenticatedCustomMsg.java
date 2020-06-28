package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.SEPA_INFO;

@Service("authenticatedCustomMsg")
public class AuthenticatedCustomMsg extends TemplateBasedOperationHandler {

    public AuthenticatedCustomMsg(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String templatePath(SandboxContext context) {
        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(SEPA_INFO))) {
            return "response-templates/authorized/custom-message-sepa-info.json";
        }

        throw new IllegalStateException("Cant't handle message: " + context.getRequestData());
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.CUSTOM_MSG;
    }
}
