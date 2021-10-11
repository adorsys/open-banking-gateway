package de.adorsys.opba.protocol.hbci.service.protocol;

import de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

/**
 * Switches execution mode from {@link ContextMode#MOCK_REAL_CALLS} (context validation to find parameters that
 * are required from user) to {@link ContextMode#REAL_CALLS} (real calls to ASPSP API)
 */
@Service("hbciUpdateToRealModeBpmnContext")
public class HbciUpdateToRealModeBpmnContext implements JavaDelegate {

    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    public void execute(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    logResolver.log("execute: execution ({}) with context ({})", execution, ctx);

                    ctx.setMode(ContextMode.REAL_CALLS);
                    ctx.setPsuPin(null);

                    logResolver.log("done execution ({}) with context ({})", execution, ctx);
                }
        );
    }
}
