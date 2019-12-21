package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.redirect;

import de.adorsys.opba.core.protocol.domain.entity.Consent;
import de.adorsys.opba.core.protocol.repository.jpa.ConsentRepository;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aFinalizeRedirectConsent")
@RequiredArgsConstructor
public class FinalizeConsent extends ValidatedExecution<Xs2aContext> {

    private final ConsentRepository consents;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        consents.save(Consent.builder().consentCode(context.getConsentId()).build());
    }
}
