package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.PaymentAcquired;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.service.xs2a.RedirectExecutor;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Redirects PSU to the page that shows that payment was granted and has button to redirect PSU back to FinTech.
 */
@Service("xs2aReportToFintechPaymentAuthorized")
@RequiredArgsConstructor
public class ReportPaymentAuthorizationFinished extends ValidatedExecution<Xs2aPisContext> {

    private final RedirectExecutor redirectExecutor;
    private final ProtocolUrlsConfiguration urlsConfiguration;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        redirectExecutor.redirect(
                execution,
                context,
                urlsConfiguration.getPis().getResult(),
                context.getFintechRedirectUriOk(),
                redirect -> new PaymentAcquired(redirect.build()));
    }
}
