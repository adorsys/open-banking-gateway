package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.config.HbciProtocolConfiguration;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.HbciRedirectExecutor;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Asks user to select SCA TAN challenge if multiple challenges are available.
 */
@Service("hbciAskToSelectTanChallenge")
@RequiredArgsConstructor
public class HbciAskToSelectTanChallenge extends ValidatedExecution<HbciContext> {

    private final RuntimeService runtimeService;
    private final HbciRedirectExecutor redirectExecutor;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        logResolver.log("Number of available SCA methods: {}", context.getAvailableSca().size());
        if (context.getAvailableSca().size() >= 2) {
            redirectExecutor.redirect(execution, context, redir -> {
                HbciProtocolConfiguration.UrlSet urlSet = ProtocolAction.SINGLE_PAYMENT.equals(context.getAction())
                        ? redir.getPis() : redir.getAis();
                return urlSet.getRedirect().getParameters().getSelectScaMethod();
            });
        } else {
            // Nothing to select by user, autoselect
            ContextUtil.getAndUpdateContext(
                    execution,
                    (HbciContext ctx) -> {
                        ctx.setUserSelectScaId(ctx.getAvailableSca().get(0).getKey());
                        ctx.setSelectedScaType(ctx.getAvailableSca().get(0).getType());
                    }
            );

            runtimeService.trigger(execution.getId());
        }
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);

        runtimeService.trigger(execution.getId());
    }
}
