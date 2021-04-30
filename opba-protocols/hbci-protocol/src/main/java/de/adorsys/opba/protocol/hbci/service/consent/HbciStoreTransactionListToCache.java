package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("hbciStoreTransactionListToCache")
@RequiredArgsConstructor
public class HbciStoreTransactionListToCache extends ValidatedExecution<TransactionListHbciContext> {

    private final HbciCachedResultAccessor hbciCachedResultAccessor;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        HbciResultCache cached = null != context.getCachedResult() ? context.getCachedResult() : new HbciResultCache();

        logResolver.log("transactionsByIban is empty: {}", null == cached.getTransactionsByIban());
        if (null == cached.getTransactionsByIban()) {
            cached.setTransactionsByIban(new HashMap<>());
        }

        cached.setConsent(context.getHbciDialogConsent());
        cached.getTransactionsByIban().put(context.getAccountIban(), context.getResponse());
        hbciCachedResultAccessor.resultToCache(context, cached);
    }
}
