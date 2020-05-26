package de.adorsys.opba.protocol.facade.services.pis;

import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoRequest;
import de.adorsys.opba.protocol.api.pis.GetPaymentInfoState;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.GET_PAYMENT_INFORMATION;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

public class GetPaymentInformationService extends FacadeService<PaymentInfoRequest, PaymentInfoBody, GetPaymentInfoState> {
    public GetPaymentInformationService(
            Map<String, ? extends GetPaymentInfoState> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate) {
        super(GET_PAYMENT_INFORMATION, actionProviders, selector, provider, handler, txTemplate);
    }
}
