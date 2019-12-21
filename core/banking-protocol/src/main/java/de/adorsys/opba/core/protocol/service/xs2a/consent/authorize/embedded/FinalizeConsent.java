package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.embedded;

import de.adorsys.opba.core.protocol.domain.entity.Consent;
import de.adorsys.opba.core.protocol.repository.jpa.ConsentRepository;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aFinalizeEmbeddedConsent")
@RequiredArgsConstructor
public class FinalizeConsent extends ValidatedExecution<Xs2aContext> {

    private final AccountInformationService ais;
    private final ConsentRepository consents;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ais.updateConsentsPsuData(
                context.getConsentId(),
                context.getAuthorizationId(),
                context.toHeaders(),
                authentication()
        );

        consents.save(Consent.builder().consentCode(context.getConsentId()).build());
    }

    private TransactionAuthorisation authentication() {
        TransactionAuthorisation authorisation = new TransactionAuthorisation();
        authorisation.setScaAuthenticationData("123456");
        return authorisation;
    }
}
