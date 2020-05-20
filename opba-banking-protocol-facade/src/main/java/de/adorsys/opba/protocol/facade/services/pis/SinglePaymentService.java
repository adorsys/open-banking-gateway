package de.adorsys.opba.protocol.facade.services.pis;

import de.adorsys.opba.protocol.api.dto.request.authorization.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.api.pis.SinglePayment;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.SINGLE_PAYMENT;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

@Service
public class SinglePaymentService extends FacadeService<InitiateSinglePaymentRequest, SinglePaymentBody, SinglePayment> {

    public SinglePaymentService(
            Map<String, ? extends SinglePayment> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate) {
        super(SINGLE_PAYMENT, actionProviders, selector, provider, handler, txTemplate);
    }
}
