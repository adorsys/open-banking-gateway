package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.redirect;

import de.adorsys.opba.core.protocol.domain.dto.RedirectResult;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("xs2aDoRedirectForScaChallenge")
@RequiredArgsConstructor
public class ScaRedirect extends ValidatedExecution<Xs2aContext> {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        RedirectResult redirect = new RedirectResult();
        redirect.setProcessId(execution.getRootProcessInstanceId());
        redirect.setRedirectUri(context.getStartScaProcessResponse().getLinks().get("scaRedirect").getHref());
        applicationEventPublisher.publishEvent(redirect);
    }
}
