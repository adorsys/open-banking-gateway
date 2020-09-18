package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciLoadAccountListFromCache")
@RequiredArgsConstructor
public class HbciReadAccountListFromCache extends ValidatedExecution<AccountListHbciContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        convertConsentToResponseIfPresent(execution);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, AccountListHbciContext context) {
        convertConsentToResponseIfPresent(execution);
    }

    @SneakyThrows
    private void convertConsentToResponseIfPresent(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(execution, (AccountListHbciContext ctx) -> {
            if (ctx.getOnline() != Boolean.TRUE && null != ctx.getCachedResult()) {
                ctx.setResponse(ctx.getCachedResult().getAccounts());
            }
        });
    }
}
