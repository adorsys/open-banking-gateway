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
 * Asks PSU for his PIN/Password by redirect him to password input page. Suspends process to wait for users' input.
 */
@Service("hbciAskForPin")
@RequiredArgsConstructor
public class HbciAskForPin extends ValidatedExecution<HbciContext> {

    private final RuntimeService runtimeService;
    private final HbciRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        redirectExecutor.redirect(execution, context,
                redir -> {
                    context.setHbciDialogConsent(null);
                    return context.getActiveUrlSet(redir).getRedirect().getParameters().getProvidePsuPassword();
                });
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        ContextUtil.getAndUpdateContext(
            execution,
            (HbciContext ctx) -> ctx.setPsuPin("mock-password")
        );
        runtimeService.trigger(execution.getId());
    }
}
