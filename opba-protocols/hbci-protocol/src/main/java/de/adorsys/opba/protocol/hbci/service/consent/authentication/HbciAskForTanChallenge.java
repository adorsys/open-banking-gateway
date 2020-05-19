package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.HbciRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Asks PSU for his SCA challenge result by redirect him to password input page. Suspends process to wait for users' input.
 */
@Service("hbciAskForTanChallenge")
@RequiredArgsConstructor
public class HbciAskForTanChallenge extends ValidatedExecution<HbciContext> {

    private final RuntimeService runtimeService;
    private final HbciRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        redirectExecutor.redirect(execution, context, redir -> redir.getParameters().getReportScaResult());
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        ContextUtil.getAndUpdateContext(
            execution,
            (HbciContext ctx) -> {
                ctx.setLastScaChallenge("mock-challenge");
            }
        );
        runtimeService.trigger(execution.getId());
    }
}
