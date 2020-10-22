package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Redirects PSU to the page that shows that consent was granted and has button to redirect PSU back to FinTech.
 */
@Service("xs2aReportToFintechConsentAuthorized")
@RequiredArgsConstructor
public class ReportConsentAuthorizationFinished extends ValidatedExecution<Xs2aContext> {

    private final Xs2aRedirectExecutor redirectExecutor;
    private final ProtocolUrlsConfiguration urlsConfiguration;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(
                execution,
                context,
                ContextUtil.buildAndExpandQueryParameters(urlsConfiguration.getAis().getWebHooks().getResult(), context).toASCIIString(),
                context.getFintechRedirectUriOk(),
                redirect -> new ConsentAcquired(redirect.build()));
    }

}
