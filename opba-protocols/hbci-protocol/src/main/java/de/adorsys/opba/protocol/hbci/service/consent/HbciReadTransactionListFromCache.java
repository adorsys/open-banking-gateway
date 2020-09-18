package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciReadTransactionListFromCache")
@RequiredArgsConstructor
public class HbciReadTransactionListFromCache extends ValidatedExecution<TransactionListHbciContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        convertConsentToResponseIfPresent(execution);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, TransactionListHbciContext context) {
        convertConsentToResponseIfPresent(execution);
    }

    @SneakyThrows
    private void convertConsentToResponseIfPresent(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(execution, (TransactionListHbciContext ctx) -> {
            if (ctx.getOnline() != Boolean.TRUE && null != ctx.getCachedResult() && null != ctx.getCachedResult().getTransactionsByIban()) {
                ctx.setResponse(ctx.getCachedResult().getTransactionsByIban().get(ctx.getAccountIban()));
            }
        });
    }
}
