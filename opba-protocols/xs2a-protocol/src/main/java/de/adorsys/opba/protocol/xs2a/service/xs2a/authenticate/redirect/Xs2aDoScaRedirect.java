package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.redirect;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.RedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Performs redirection to the ASPSP by sending him to the page with redirection button (to ASPSP) for the redirect approach.
 */
@Service("xs2aDoRedirectForScaChallenge")
@RequiredArgsConstructor
public class Xs2aDoScaRedirect extends ValidatedExecution<Xs2aContext> {

    private final ProtocolConfiguration configuration;
    private final RuntimeService runtimeService;
    private final RedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(
                execution,
                context,
                configuration.getRedirect().getAis().getToAspsp(),
                context.getStartScaProcessResponse().getLinks().get("scaRedirect").getHref()
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
    }
}
