package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("hbciStoreTransactionListToCache")
@RequiredArgsConstructor
public class HbciStoreTransactionListToCache extends ValidatedExecution<TransactionListHbciContext> {

    private final HbciCachedResultAccessor hbciCachedResultAccessor;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        HbciResultCache cached = hbciCachedResultAccessor.resultFromCache(context).orElseGet(HbciResultCache::new);
        if (null == cached.getTransactionsByIban()) {
            cached.setTransactionsByIban(new HashMap<>());
        }

        cached.getTransactionsByIban().put(context.getAccountIban(), context.getResponse());
        hbciCachedResultAccessor.resultToCache(context, cached);
    }
}
