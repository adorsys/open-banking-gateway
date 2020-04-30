package de.adorsys.opba.protocol.xs2a.service.xs2a.ais.sandbox;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.AccountListingService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Special service that does not publish account listing result anywhere. Not a service, so that context is not
 * polluted.
 */
class NoResponseAccountListingService extends AccountListingService {

    NoResponseAccountListingService(
            Extractor extractor, Xs2aValidator validator, AccountInformationService ais
    ) {
        super(new BlackHoleEventPublisher(), extractor, validator, ais);
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        super.doRealExecution(execution, context);
    }

    private static class BlackHoleEventPublisher implements ApplicationEventPublisher {

        @Override
        public void publishEvent(Object event) {
            // NOP
        }
    }
}
