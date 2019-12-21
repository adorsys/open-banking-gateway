package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.embedded;

import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Slf4j
@Service("xs2aScaExemption")
public class ScaExemption extends ValidatedExecution<Xs2aContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        log.info("SCA Exemption");
    }
}
