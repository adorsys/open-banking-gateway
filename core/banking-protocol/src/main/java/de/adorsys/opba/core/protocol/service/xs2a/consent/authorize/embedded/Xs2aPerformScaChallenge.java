package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.embedded;

import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.RedirectExecutor;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aPerformScaChallenge")
@RequiredArgsConstructor
public class Xs2aPerformScaChallenge extends ValidatedExecution<Xs2aContext> {

    private final RuntimeService runtimeService;
    private final RedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(execution, context, redir -> redir.getParameters().getReportScaResult());
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
    }
}
