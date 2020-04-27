package de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.RedirectExecutor;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.service.xs2a.consent.ConsentConst.CONSENT_FINALIZED;

/**
 * Asks PSU for his SCA challenge result by redirect him to password input page. Suspends process to wait for users' input.
 */
@Service("xs2aAskForScaChallenge")
@RequiredArgsConstructor
public class Xs2aAskForScaChallenge extends ValidatedExecution<Xs2aContext> {

    private final RuntimeService runtimeService;
    private final RedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(execution, context, redir -> redir.getParameters().getReportScaResult());
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        ContextUtil.getAndUpdateContext(
            execution,
            (Xs2aContext ctx) -> {
                ctx.setLastScaChallenge("mock-challenge");
                ctx.setScaStatus(CONSENT_FINALIZED);
            }
        );
        runtimeService.trigger(execution.getId());
    }
}
