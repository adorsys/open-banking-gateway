package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import de.adorsys.opba.protocol.sandbox.hbci.config.dto.SensitiveAuthLevel;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.MapRegexUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.RequestStatusUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.SEPA_INFO;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.TRANSACTIONS;

@Service("authenticatedCustomMsg")
public class AuthenticatedCustomMsg extends TemplateBasedOperationHandler {

    public AuthenticatedCustomMsg(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(SandboxContext context) {
        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(SEPA_INFO))) {
            return context.getBank().getSecurity().getAccounts() == SensitiveAuthLevel.AUTHENTICATED ?
                    "response-templates/authenticated/custom-message-sepa-info.json"
                    : "response-templates/authenticated/custom-message-authorization-required.json";
        }

        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(TRANSACTIONS))) {
            if (context.getBank().getSecurity().getTransactions() == SensitiveAuthLevel.AUTHENTICATED) {
                return "response-templates/authenticated/custom-message-konto-mt940.json";
            }

            if (RequestStatusUtil.isForTransactionListing(context.getRequestData())) {
                context.setAccountNumberRequestedBeforeSca(MapRegexUtil.getDataRegex(context.getRequestData(), "TAN2Step6\\.OrderAccount\\.number"));
            }

            return "response-templates/authenticated/custom-message-authorization-required.json";
        }

        throw new IllegalStateException("Cant't handle message: " + context.getRequestData());
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.CUSTOM_MSG;
    }
}
