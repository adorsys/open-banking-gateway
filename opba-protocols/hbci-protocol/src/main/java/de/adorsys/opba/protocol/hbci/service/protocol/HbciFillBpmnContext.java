package de.adorsys.opba.protocol.hbci.service.protocol;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

/**
 * Fills key BPMN execution parameters into context. Should be called as first element of process execution.
 * Sets {@link ContextMode} to {@link ContextMode#MOCK_REAL_CALLS}, so that the execution is initiated in
 * validation mode to find what parameters are necessary to be provided by PSU during authorization.
 */
@Service("hbciFillBpmnContext")
public class HbciFillBpmnContext implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (BaseContext ctx) -> {
                    ctx.setMode(ContextMode.MOCK_REAL_CALLS);
                    ctx.setSagaId(execution.getRootProcessInstanceId());
                }
        );
    }
}
