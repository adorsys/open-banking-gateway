package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Reports that available consent is incompatible with the request.
 */
@Service("hbciReportConsentIncompatible")
@RequiredArgsConstructor
public class HbciReportConsentIncompatible extends ValidatedExecution<HbciContext> {

    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);

        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setConsentIncompatible(true));
    }
}
