package de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.RedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Asks PSU to select SCA challenge method from the list by redirecting him to the page with SCA method selection.
 */
@Service("xs2aAskForSelectedScaMethod")
@RequiredArgsConstructor
public class Xs2aAskForSelectedScaMethod extends ValidatedExecution<Xs2aContext> {

    private final RuntimeService runtimeService;
    private final RedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(execution, context, redir -> redir.getParameters().getSelectScaMethod());
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
    }
}
