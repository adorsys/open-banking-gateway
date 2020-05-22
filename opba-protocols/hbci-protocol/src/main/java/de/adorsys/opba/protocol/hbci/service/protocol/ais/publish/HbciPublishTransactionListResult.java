package de.adorsys.opba.protocol.hbci.service.protocol.ais.publish;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("hbciPublishTransactionListResult")
@RequiredArgsConstructor
public class HbciPublishTransactionListResult extends ValidatedExecution<TransactionListHbciContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        eventPublisher.publishEvent(
                new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), context.getResponse())
        );

        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setViolations(null));
    }
}
