package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.config.HbciProtocolConfiguration;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.HbciRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Asks PSU for his PIN/Password by redirect him to password input page. Suspends process to wait for users' input.
 */
@Service("hbciAskForTan")
@RequiredArgsConstructor
public class HbciAskForTan extends ValidatedExecution<HbciContext> {

    private final RuntimeService runtimeService;
    private final HbciRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        redirectExecutor.redirect(execution, context, redir -> {
            HbciProtocolConfiguration.UrlSet urlSet = ProtocolAction.SINGLE_PAYMENT.equals(context.getAction())
                    ? redir.getPis() : redir.getAis();
            return urlSet.getRedirect().getParameters().getReportScaResult();
        });
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        ContextUtil.getAndUpdateContext(
            execution,
            (HbciContext ctx) -> ctx.setPsuTan("mock-password")
        );
        runtimeService.trigger(execution.getId());
    }
}
