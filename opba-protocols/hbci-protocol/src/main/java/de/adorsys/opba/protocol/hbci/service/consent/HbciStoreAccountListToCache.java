package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service("hbciStoreAccountListToCache")
@RequiredArgsConstructor
public class HbciStoreAccountListToCache extends ValidatedExecution<AccountListHbciContext> {

    private final HbciCachedResultAccessor hbciCachedResultAccessor;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        HbciResultCache  cached = null != context.getCachedResult() ? context.getCachedResult() : new HbciResultCache();
        if (null != cached.getCachedAt()) {
            cached.setCachedAt(Instant.now());
        }
        cached.setAccounts(context.getResponse());
        cached.setConsent(context.getHbciDialogConsent());
        hbciCachedResultAccessor.resultToCache(context, cached);
    }
}
