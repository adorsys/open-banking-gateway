package de.adorsys.opba.protocol.hbci.service.protocol.ais.publish;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.TransactionUtil;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service("hbciPublishTransactionListResult")
@RequiredArgsConstructor
public class HbciPublishTransactionListResult extends ValidatedExecution<TransactionListHbciContext> {

    private final ApplicationEventPublisher eventPublisher;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        var result = context.getResponse();
        if (null != result.getBookings()) {
            result.setBookings(
                    result.getBookings().stream()
                            .filter(it -> TransactionUtil.isWithinRange(it.getBookingDate(), context.getDateFrom(), context.getDateTo()))
                            .collect(Collectors.toList())
            );
        }

        eventPublisher.publishEvent(
                new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), context.getResponse())
        );

        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setViolations(null));
    }
}
