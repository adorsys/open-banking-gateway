package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
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
    private final Xs2aRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(execution, context, urls -> {
            ProtocolUrlsConfiguration.UrlSet urlSet = ProtocolAction.SINGLE_PAYMENT.equals(context.getAction())
                    ? urls.getPis() : urls.getAis();
            return urlSet.getParameters().getReportScaResult();
        });
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
