package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize;

import de.adorsys.opba.core.protocol.domain.entity.Consent;
import de.adorsys.opba.core.protocol.repository.jpa.ConsentRepository;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("finalizeConsent")
@RequiredArgsConstructor
public class FinalizeConsent implements JavaDelegate {

    private final AccountInformationService ais;
    private final ConsentRepository consents;

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        Xs2aContext context = delegateExecution.getVariable(CONTEXT, Xs2aContext.class);

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
