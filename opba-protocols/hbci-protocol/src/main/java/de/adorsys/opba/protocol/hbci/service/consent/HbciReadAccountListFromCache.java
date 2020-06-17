package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("hbciReadAccountListFromCache")
@RequiredArgsConstructor
public class HbciReadAccountListFromCache extends ValidatedExecution<AccountListHbciContext> {

    private final HbciCachedResultAccessor cachedResultReader;

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        convertConsentToResponseIfPresent(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, AccountListHbciContext context) {
        convertConsentToResponseIfPresent(execution, context);
    }

    @SneakyThrows
    private void convertConsentToResponseIfPresent(DelegateExecution execution, AccountListHbciContext context) {
        Optional<HbciResultCache> hbciResult = cachedResultReader.resultFromCache(context);

        if (!hbciResult.isPresent()) {
            return;
        }

        ContextUtil.getAndUpdateContext(
                execution,
                (AccountListHbciContext toUpdate) -> {
                    toUpdate.setResponse(hbciResult.get().getAccounts());
                    toUpdate.setHbciDialogConsent(hbciResult.get().getConsent());
                }
        );
    }
}
