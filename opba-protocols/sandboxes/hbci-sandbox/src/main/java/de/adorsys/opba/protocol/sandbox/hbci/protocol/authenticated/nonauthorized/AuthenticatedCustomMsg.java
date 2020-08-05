package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import com.google.common.hash.Hashing;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.SensitiveAuthLevel;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.MapRegexUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.RequestStatusUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import de.adorsys.opba.protocol.sandbox.hbci.service.HbciSandboxPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.PAYMENT;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.PAYMENT_STATUS;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.SEPA_INFO;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.TRANSACTIONS;

@Slf4j
@Service("authenticatedCustomMsg")
public class AuthenticatedCustomMsg extends TemplateBasedOperationHandler {

    private final HbciSandboxPaymentService paymentService;

    public AuthenticatedCustomMsg(JsonTemplateInterpolation interpolation, HbciSandboxPaymentService paymentService) {
        super(interpolation);
        this.paymentService = paymentService;
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(HbciSandboxContext context) {
        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(SEPA_INFO))) {
            return context.getBank().getSecurity().getAccounts() == SensitiveAuthLevel.AUTHENTICATED
                    ? "response-templates/authenticated/custom-message-sepa-info.json"
                    : getAuthorizationRequiredTemplateOrWrongTanMethod();
        }

        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(TRANSACTIONS))) {
            if (context.getBank().getSecurity().getTransactions() == SensitiveAuthLevel.AUTHENTICATED) {
                return "response-templates/authenticated/custom-message-konto-mt940.json";
            }
            if (RequestStatusUtil.isForTransactionListing(context.getRequestData())) {
                context.setAccountNumberRequestedBeforeSca(MapRegexUtil.getDataRegex(context.getRequestData(), "TAN2Step6\\.OrderAccount\\.number"));
            }
            return getAuthorizationRequiredTemplateOrWrongTanMethod();
        }

        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(PAYMENT))) {
            if (context.getBank().getSecurity().getPayment() == SensitiveAuthLevel.AUTHENTICATED) {
                paymentService.createPaymentIfNeededAndPossibleFromContext(context);
                return "response-templates/authenticated/custom-message-konto-mt940.json";
            }
            if (RequestStatusUtil.isForPayment(context.getRequestData())) {
                context.setAccountNumberRequestedBeforeSca(MapRegexUtil.getDataRegex(context.getRequestData(), "TAN2Step6\\.OrderAccount\\.number"));
                setOrderReference(context);
                paymentService.createPayment(context);
            }
            return "response-templates/authenticated/custom-message-authorization-required-payment.json";
        }

        if (context.getRequestData().keySet().stream().anyMatch(it -> it.startsWith(PAYMENT_STATUS))) {
            String paymentId = MapRegexUtil.getDataRegex(context.getRequestData(), "GV\\.InstantUebSEPAStatus\\d\\.orderid");

            if (context.getBank().getSecurity().getPaymentStatus() == SensitiveAuthLevel.AUTHENTICATED) {
                paymentService.paymentFromDatabaseToContext(context, paymentId);
                return "response-templates/authenticated/custom-message-payment-status.json";
            }

            if (RequestStatusUtil.isForPaymentStatus(context.getRequestData())) {
                context.setPaymentId(paymentId);
            }
            return "response-templates/authenticated/custom-message-authorization-required-payment-status.json";
        }

        throw new IllegalStateException("Cant't handle message: " + context.getRequestData());
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.CUSTOM_MSG;
    }

    private String getAuthorizationRequiredTemplateOrWrongTanMethod() {
        return "response-templates/authenticated/custom-message-authorization-required.json";
    }

    private void setOrderReference(HbciSandboxContext context) {
        // something different from uuid to distinguish in logs
        UUID orderRef = UUID.randomUUID();
        context.setOrderReference(
                Hashing.goodFastHash(16).hashLong(orderRef.getLeastSignificantBits()).toString()
                        + "." + Hashing.goodFastHash(16).hashLong(orderRef.getMostSignificantBits()).toString()
        );
    }
}
