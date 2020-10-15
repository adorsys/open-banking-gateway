package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aCheckDecoupledAuthorizationStatus")
@RequiredArgsConstructor
public class Xs2aCheckDecoupledAuthorisationStatus extends ValidatedExecution<Xs2aPisContext> {
    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {

    }
}
