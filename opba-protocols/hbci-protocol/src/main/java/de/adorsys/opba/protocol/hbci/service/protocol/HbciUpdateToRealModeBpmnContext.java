package de.adorsys.opba.protocol.hbci.service.protocol;

import de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

/**
 * Switches execution mode from {@link ContextMode#MOCK_REAL_CALLS} (context validation to find parameters that
 * are required from user) to {@link ContextMode#REAL_CALLS} (real calls to ASPSP API)
 */
@Service("hbciUpdateToRealModeBpmnContext")
public class HbciUpdateToRealModeBpmnContext implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    ctx.setMode(ContextMode.REAL_CALLS);
                    ctx.setPsuPin(null);
                }
        );
    }
}
