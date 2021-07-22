package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.bpmnshared.dto.context.ProtocolResultCache;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciStoreAccountListToCache")
@RequiredArgsConstructor
public class HbciStoreAccountListToCache extends ValidatedExecution<AccountListHbciContext> {

    private final HbciCachedResultAccessor hbciCachedResultAccessor;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ProtocolResultCache cached = null != context.getCachedResult() ? context.getCachedResult() : new ProtocolResultCache();
        cached.setAccounts(context.getResponse());
        cached.setConsent(context.getHbciDialogConsent());
        hbciCachedResultAccessor.resultToCache(context, cached);
    }
}
