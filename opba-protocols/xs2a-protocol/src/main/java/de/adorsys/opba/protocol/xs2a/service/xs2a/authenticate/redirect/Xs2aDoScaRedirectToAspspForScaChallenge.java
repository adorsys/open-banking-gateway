package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.redirect;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.RedirectToAspsp;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Performs redirection to the ASPSP by sending him to the page with redirection button (to ASPSP) for the redirect approach.
 */
@Service("xs2aDoRedirectToAspspForScaChallenge")
@RequiredArgsConstructor
public class Xs2aDoScaRedirectToAspspForScaChallenge extends ValidatedExecution<Xs2aContext> {

    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final RuntimeService runtimeService;
    private final Xs2aRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ProtocolUrlsConfiguration.UrlSet urlSet = urlsConfiguration.getUrlAisOrPisSetBasedOnContext(context);

        redirectExecutor.redirect(
                execution,
                context,
                urlSet.getToAspsp(),
                context.getStartScaProcessResponse().getLinks().get("scaRedirect").getHref(),
                redirect -> new RedirectToAspsp(redirect.build())
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
    }
}
