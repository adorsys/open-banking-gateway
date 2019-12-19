package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.redirect;

import de.adorsys.opba.core.protocol.domain.entity.Consent;
import de.adorsys.opba.core.protocol.repository.jpa.ConsentRepository;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aFinalizeRedirectConsent")
@RequiredArgsConstructor
public class FinalizeConsent implements JavaDelegate {

    private final ConsentRepository consents;

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        Xs2aContext context = delegateExecution.getVariable(CONTEXT, Xs2aContext.class);
        consents.save(Consent.builder().consentCode(context.getConsentId()).build());
    }
}
