package de.adorsys.opba.protocol.xs2a.service.protocol;

import de.adorsys.opba.protocol.xs2a.context.ContextMode;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

/**
 * Switches execution mode from {@link ContextMode#MOCK_REAL_CALLS} (context validation to find parameters that
 * are required from user) to {@link ContextMode#REAL_CALLS} (real calls to ASPSP API)
 */
@Service("xs2aUpdateToRealModeBpmnContext")
public class Xs2aUpdateToRealModeBpmnContext implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setMode(ContextMode.REAL_CALLS);
                    ctx.setConsentId(null);
                }
        );
    }
}
