package de.adorsys.opba.protocol.xs2a.service.xs2a;

import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Redirect;
import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URI;
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
        String uiScreenUri = redirectSelector.apply(configuration.getRedirect());
        redirect(execution, context, uiScreenUri, uiScreenUri);
    }

    public void redirect(
            DelegateExecution execution,
            Xs2aContext context,
            String uiScreenUriSpel,
            String destinationUri) {
        URI screenUri = URI.create(
            ContextUtil.evaluateSpelForCtx(
                uiScreenUriSpel,
                execution,
                context
            )
        );
        Redirect.RedirectBuilder redirect = Redirect.builder();
        redirect.processId(execution.getRootProcessInstanceId());
        redirect.executionId(execution.getId());
        redirect.redirectUri(screenUri);
        ContextUtil.getAndUpdateContext(
            execution,
            (BaseContext ctx) -> {
                LastRedirectionTarget target = new LastRedirectionTarget();
                target.setRedirectTo(destinationUri);
                target.setRedirectToUiScreen(screenUri.toASCIIString());
                ctx.setLastRedirection(target);
            }
        );

        applicationEventPublisher.publishEvent(redirect.build());
    }
}
