package de.adorsys.opba.protocol.xs2a.service.protocol;

import de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

/**
 * Switches execution mode from {@link ContextMode#MOCK_REAL_CALLS} (context validation to find parameters that
 * are required from user) to {@link ContextMode#REAL_CALLS} (real calls to ASPSP API)
 */
@Service("xs2aUpdateToRealModeBpmnContext")
public class Xs2aUpdateToRealModeBpmnContext implements JavaDelegate {

    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    public void execute(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    logResolver.log("execute: execution ({}) with context ({})", execution, ctx);

                    ctx.setMode(ContextMode.REAL_CALLS);
                    ctx.setConsentId(null);

                    logResolver.log("done execution ({}) with context ({})", execution, ctx);
                }
        );
    }
}
