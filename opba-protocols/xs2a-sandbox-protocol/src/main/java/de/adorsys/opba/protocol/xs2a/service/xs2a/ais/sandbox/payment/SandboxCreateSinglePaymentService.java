package de.adorsys.opba.protocol.xs2a.service.xs2a.ais.sandbox.payment;

import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateConsentOrPaymentPossibleErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.payment.CreateSinglePaymentService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.api.PaymentInitiationService;
import org.springframework.stereotype.Service;

@Service("xs2aSandboxCreateSinglePaymentService")
public class SandboxCreateSinglePaymentService extends CreateSinglePaymentService {

    public SandboxCreateSinglePaymentService(PaymentInitiationService pis, Xs2aValidator validator,
                                             ProtocolUrlsConfiguration urlsConfiguration,
                                             CreateConsentOrPaymentPossibleErrorHandler handler, Extractor extractor) {
        super(pis, validator, urlsConfiguration, handler, extractor);
    }
}
