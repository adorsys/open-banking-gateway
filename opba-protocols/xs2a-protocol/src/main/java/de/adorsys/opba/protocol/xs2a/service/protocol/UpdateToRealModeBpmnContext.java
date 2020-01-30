package de.adorsys.opba.protocol.xs2a.service.protocol;

import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ContextMode;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("updateToRealModeBpmnContext")
public class UpdateToRealModeBpmnContext implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (BaseContext ctx) -> ctx.setMode(ContextMode.REAL_CALLS)
        );
    }
}
