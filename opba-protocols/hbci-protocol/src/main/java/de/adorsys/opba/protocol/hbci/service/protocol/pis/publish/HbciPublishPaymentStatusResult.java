package de.adorsys.opba.protocol.hbci.service.protocol.pis.publish;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("hbciPublishPaymentStatusResult")
@RequiredArgsConstructor
public class HbciPublishPaymentStatusResult extends ValidatedExecution<PaymentHbciContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected void doRealExecution(DelegateExecution execution, PaymentHbciContext context) {
        eventPublisher.publishEvent(
                new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), context.getPayment())
        );

        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setViolations(null));
    }
}
