package de.adorsys.opba.protocol.xs2a.service.xs2a;

import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Redirect;
import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RedirectExecutor {

    private final ProtocolConfiguration configuration;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void redirect(
            DelegateExecution execution,
            Xs2aContext context,
            Function<ProtocolConfiguration.Redirect, String> redirectSelector) {
        redirect(execution, context, redirectSelector.apply(configuration.getRedirect()));
    }

    public void redirect(
            DelegateExecution execution,
            Xs2aContext context,
            String destinationSpel) {
        Redirect redirect = new Redirect();
        redirect.setProcessId(execution.getRootProcessInstanceId());
        redirect.setRedirectUri(
                ContextUtil.evaluateSpelForCtx(
                        destinationSpel,
                        execution,
                        context
                )
        );

        applicationEventPublisher.publishEvent(redirect);
    }
}
