package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.redirect;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("xs2aDoRedirectForScaChallenge")
public class ScaRedirect implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
    }
}
