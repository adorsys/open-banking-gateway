package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.authorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.RequestStatusUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.PAYMENT;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.SEPA_INFO;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.TRANSACTIONS;

@Service("authorizedCustomMsg")
public class AuthorizedCustomMsg extends TemplateBasedOperationHandler {

    public AuthorizedCustomMsg(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(HbciSandboxContext context) {
        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(SEPA_INFO))
                || RequestStatusUtil.isForAccountListing(context.getRequestData())) {
            return "response-templates/authorized/custom-message-sepa-info.json";
        }

        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(TRANSACTIONS))
                || RequestStatusUtil.isForTransactionListing(context.getRequestData())) {
            return "response-templates/authorized/custom-message-konto-mt940.json";
        }

        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(PAYMENT))
                || RequestStatusUtil.isForPayment(context.getRequestData())) {
            return "response-templates/authorized/custom-message-payment-response.json";
        }

        throw new IllegalStateException("Cant't handle message: " + context.getRequestData());
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.CUSTOM_MSG;
    }
}
