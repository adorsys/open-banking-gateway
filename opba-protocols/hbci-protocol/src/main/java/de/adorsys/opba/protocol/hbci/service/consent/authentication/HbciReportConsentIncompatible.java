package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Reports that available consent is incompatible with the request.
 */
@Service("hbciReportConsentIncompatible")
@RequiredArgsConstructor
public class HbciReportConsentIncompatible extends ValidatedExecution<HbciContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setConsentIncompatible(true));
    }
}
