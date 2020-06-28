package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.authorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.SEPA_INFO;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.TRANSACTIONS;

@Service("authorizedCustomMsg")
public class AuthorizedCustomMsg extends TemplateBasedOperationHandler {

    public AuthorizedCustomMsg(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String templatePath(SandboxContext context) {
        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(SEPA_INFO))) {
            return "response-templates/authorized/custom-message-sepa-info.json";
        }

        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(TRANSACTIONS))) {
            return "response-templates/authorized/custom-message-konto-mt940.json";
        }

        throw new IllegalStateException("Cant't handle message: " + context.getRequestData());
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.CUSTOM_MSG;
    }
}
