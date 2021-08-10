package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.redirect;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.RedirectToAspsp;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
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

    public static final String SCA_REDIRECT = "scaRedirect";

    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final RuntimeService runtimeService;
    private final Xs2aRedirectExecutor redirectExecutor;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ProtocolUrlsConfiguration.UrlSet urlSet = urlsConfiguration.getUrlAisOrPisSetBasedOnContext(context);

        redirectExecutor.redirect(
                execution,
                context,
                urlSet.getToAspsp(),
                getRedirectToAspspUrl(context),
                redirect -> new RedirectToAspsp(redirect.build())
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);

        runtimeService.trigger(execution.getId());
    }

    protected String getRedirectToAspspUrl(Xs2aContext context) {
        return context.getStartScaProcessResponse().getLinks().get(SCA_REDIRECT).getHref();
    }
}
