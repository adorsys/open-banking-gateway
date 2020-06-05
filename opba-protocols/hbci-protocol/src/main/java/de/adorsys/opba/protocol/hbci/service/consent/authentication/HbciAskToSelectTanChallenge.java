package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.HbciRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Asks user to select SCA TAN challenge if multiple challenges are available.
 */
@Service("hbciAskToSelectTanChallenge")
@RequiredArgsConstructor
public class HbciAskToSelectTanChallenge extends ValidatedExecution<HbciContext> {

    private final RuntimeService runtimeService;
    private final HbciRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        runtimeService.trigger(execution.getId());
    }
}
