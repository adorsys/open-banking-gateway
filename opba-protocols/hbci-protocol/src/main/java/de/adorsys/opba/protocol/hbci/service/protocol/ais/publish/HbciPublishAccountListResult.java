package de.adorsys.opba.protocol.hbci.service.protocol.ais.publish;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("hbciPublishAccountListResult")
@RequiredArgsConstructor
public class HbciPublishAccountListResult extends ValidatedExecution<AccountListHbciContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        eventPublisher.publishEvent(
                new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), context.getResponse())
        );

        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setViolations(null));
    }
}
