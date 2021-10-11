package de.adorsys.opba.protocol.xs2a.service.xs2a.ais.sandbox;

import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.Xs2aCachedResultAccessor;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.Xs2aAccountListingService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.Xs2aConsentErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Special service that does not publish account listing result anywhere. Not a service, so that context is not
 * polluted.
 */
class NoResponseXs2aAccountListingService extends Xs2aAccountListingService {

    NoResponseXs2aAccountListingService(
            Extractor extractor, Xs2aValidator validator, AccountInformationService ais,
            Xs2aConsentErrorHandler consentErrorHandler, Xs2aCachedResultAccessor accessor
    ) {
        super(new BlackHoleEventPublisher(), extractor, validator, ais, consentErrorHandler, accessor);
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aAisContext context) {
        super.doRealExecution(execution, context);
    }

    private static class BlackHoleEventPublisher implements ApplicationEventPublisher {

        @Override
        public void publishEvent(Object event) {
            // NOP
        }
    }
}
