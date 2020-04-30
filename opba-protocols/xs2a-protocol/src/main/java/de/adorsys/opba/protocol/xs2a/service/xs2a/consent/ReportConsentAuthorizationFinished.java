package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.RedirectExecutor;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Redirects PSU to the page that shows that consent was granted and has button to redirect PSU back to FinTech.
 */
@Service("xs2aReportToFintechConsentAuthorized")
@RequiredArgsConstructor
public class ReportConsentAuthorizationFinished extends ValidatedExecution<Xs2aContext> {

    private final RedirectExecutor redirectExecutor;
    private final ProtocolConfiguration configuration;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(
            execution,
            context,
            configuration.getRedirect().getConsentAccounts().getResult(),
            context.getFintechRedirectUriOk(),
            redirect -> new ConsentAcquired(redirect.build()));
    }
}
