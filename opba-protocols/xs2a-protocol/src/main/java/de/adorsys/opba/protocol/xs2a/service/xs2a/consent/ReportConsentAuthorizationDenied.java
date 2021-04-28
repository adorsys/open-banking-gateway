package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aReportToFintechConsentDenied")
@RequiredArgsConstructor
public class ReportConsentAuthorizationDenied extends ValidatedExecution<Xs2aContext> {

    private final Xs2aRedirectExecutor redirectExecutor;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        redirectExecutor.redirect(
            execution,
            context,
            context.getFintechRedirectUriNok(),
            context.getFintechRedirectUriNok(),
            redirect -> new ConsentAcquired(redirect.build()));
    }
}
