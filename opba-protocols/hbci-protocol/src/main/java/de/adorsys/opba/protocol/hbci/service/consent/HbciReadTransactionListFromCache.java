package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("hbciReadTransactionListFromCache")
@RequiredArgsConstructor
public class HbciReadTransactionListFromCache extends ValidatedExecution<TransactionListHbciContext> {

    private final HbciCachedResultAccessor cachedResultReader;

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        convertConsentToResponseIfPresent(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, TransactionListHbciContext context) {
        convertConsentToResponseIfPresent(execution, context);
    }

    @SneakyThrows
    private void convertConsentToResponseIfPresent(DelegateExecution execution, TransactionListHbciContext context) {
        Optional<HbciResultCache> hbciResult = cachedResultReader.resultFromCache(context);

        if (!hbciResult.isPresent() || null == hbciResult.get().getTransactionsByIban()) {
            return;
        }

        AisListTransactionsResult transactions = hbciResult.get().getTransactionsByIban().get(context.getAccountIban());

        if (null == transactions) {
            return;
        }

        ContextUtil.getAndUpdateContext(
                execution,
                (TransactionListHbciContext toUpdate) -> toUpdate.setResponse(transactions)
        );
    }
}
