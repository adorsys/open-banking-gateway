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
        redirect(execution, context, uiScreenUri, null);
    }

    public void redirect(
        DelegateExecution execution,
        Xs2aContext context,
        String uiScreenUriSpel,
        String destinationUri
    ) {
        redirect(execution, context, uiScreenUriSpel, destinationUri, Redirect.RedirectBuilder::build);
    }

    public void redirect(
        DelegateExecution execution,
        Xs2aContext context,
        String uiScreenUriSpel,
        String destinationUri,
        Function<Redirect.RedirectBuilder, Object> eventFactory
    ) {
        setDestinationUriInContext(execution, destinationUri);

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

        setUiUriInContext(execution, screenUri);

        applicationEventPublisher.publishEvent(eventFactory.apply(redirect));
    }

    private void setUiUriInContext(DelegateExecution execution, URI screenUri) {
        ContextUtil.getAndUpdateContext(
            execution,
            (BaseContext ctx) -> {
                LastRedirectionTarget target = getOrCreateLastRedirection(ctx);
                target.setRedirectToUiScreen(screenUri.toASCIIString());
                ctx.setLastRedirection(target);
            }
        );
    }

    private void setDestinationUriInContext(DelegateExecution execution, String destinationUri) {
        ContextUtil.getAndUpdateContext(
            execution,
            (BaseContext ctx) -> {
                LastRedirectionTarget target = getOrCreateLastRedirection(ctx);
                target.setRedirectTo(destinationUri);
                ctx.setLastRedirection(target);
            }
        );
    }

    private LastRedirectionTarget getOrCreateLastRedirection(BaseContext ctx) {
        LastRedirectionTarget target = null == ctx.getLastRedirection() ? new LastRedirectionTarget() : ctx.getLastRedirection();
        target.setEncryption(ctx.getEncryption());
        return target;
    }
}
