package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.redirect;

import de.adorsys.opba.core.protocol.domain.dto.RedirectResult;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aDoRedirectForScaChallenge")
@RequiredArgsConstructor
public class ScaRedirect implements JavaDelegate {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        Xs2aContext context = delegateExecution.getVariable(CONTEXT, Xs2aContext.class);
        RedirectResult redirect = new RedirectResult();
        redirect.setProcessId(delegateExecution.getRootProcessInstanceId());
        redirect.setRedirectUri(context.getStartScaProcessResponse().getLinks().get("scaRedirect").getHref());
        applicationEventPublisher.publishEvent(redirect);
    }
}
