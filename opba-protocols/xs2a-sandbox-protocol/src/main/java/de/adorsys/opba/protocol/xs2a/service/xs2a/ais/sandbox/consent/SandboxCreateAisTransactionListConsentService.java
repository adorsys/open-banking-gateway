package de.adorsys.opba.protocol.xs2a.service.xs2a.ais.sandbox.consent;

import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.AisConsentInitiateExtractor;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateAisConsentService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateAisTransactionListConsentService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateConsentOrPaymentPossibleErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import org.springframework.stereotype.Service;

@Service("xs2aSandboxCreateAisTransactionListConsentService")
public class SandboxCreateAisTransactionListConsentService extends CreateAisTransactionListConsentService {

    public SandboxCreateAisTransactionListConsentService(AisConsentInitiateExtractor extractor, AccountInformationService ais,
                                                         Xs2aValidator validator, ProtocolUrlsConfiguration urlsConfiguration,
                                                         CreateConsentOrPaymentPossibleErrorHandler handler,
                                                         CreateAisConsentService createAisConsentService) {
        super(extractor, ais, validator, urlsConfiguration, handler, createAisConsentService);
    }
}
