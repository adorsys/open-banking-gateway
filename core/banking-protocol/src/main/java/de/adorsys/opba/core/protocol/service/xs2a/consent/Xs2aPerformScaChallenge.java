package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aPerformScaChallenge")
public class Xs2aPerformScaChallenge extends ValidatedExecution<Xs2aContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
    }
}
