package de.adorsys.opba.protocol.xs2a.service.protocol;

import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ContextMode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("updateToRealModeBpmnContext")
public class UpdateToRealModeBpmnContext implements JavaDelegate {

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
