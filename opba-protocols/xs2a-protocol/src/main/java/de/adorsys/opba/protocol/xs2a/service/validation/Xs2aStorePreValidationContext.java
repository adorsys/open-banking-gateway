package de.adorsys.opba.protocol.xs2a.service.validation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.BEFORE_VALIDATION_CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

@Service("xs2aStorePreValidationContext")
public class Xs2aStorePreValidationContext implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(BEFORE_VALIDATION_CONTEXT, execution.getVariable(CONTEXT));
    }
}
