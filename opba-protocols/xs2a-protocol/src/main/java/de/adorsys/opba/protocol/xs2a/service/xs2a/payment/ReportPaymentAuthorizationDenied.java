package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.PaymentAcquired;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.RedirectExecutor;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aReportToFintechPaymentDenied")
@RequiredArgsConstructor
public class ReportPaymentAuthorizationDenied extends ValidatedExecution<Xs2aPisContext> {

    private final RedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        redirectExecutor.redirect(
                execution,
                context,
                context.getFintechRedirectUriNok(),
                context.getFintechRedirectUriNok(),
                redirect -> new PaymentAcquired(redirect.build()));
    }
}
