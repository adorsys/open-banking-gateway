package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("hbciLoadConsentUnderFintechKey")
@RequiredArgsConstructor
public class HbciLoadConsentUnderFinTechKey extends ValidatedExecution<HbciContext> {

    private final HbciCachedResultAccessor resultAccessor;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        Optional<HbciResultCache> result = resultAccessor.resultFromCache(context);

        result.ifPresent(cached -> {
            ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> {
                ctx.setHbciDialogConsent(cached.getConsent());
                ctx.setCachedResult(cached);
            });
        });
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
    }
}
