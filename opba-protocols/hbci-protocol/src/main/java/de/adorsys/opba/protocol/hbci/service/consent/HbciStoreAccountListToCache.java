package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciStoreAccountListToCache")
@RequiredArgsConstructor
public class HbciStoreAccountListToCache extends ValidatedExecution<AccountListHbciContext> {

    private final HbciCachedResultAccessor hbciCachedResultAccessor;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        HbciResultCache cached = null != context.getCachedResult() ? context.getCachedResult() : new HbciResultCache();
        cached.setAccounts(context.getResponse());
        cached.setConsent(context.getHbciDialogConsent());
        hbciCachedResultAccessor.resultToCache(context, cached);
    }
}
