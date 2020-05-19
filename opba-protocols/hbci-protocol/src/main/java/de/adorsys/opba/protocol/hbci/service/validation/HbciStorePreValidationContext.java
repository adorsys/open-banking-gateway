package de.adorsys.opba.protocol.hbci.service.validation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.BEFORE_VALIDATION_CONTEXT;

/**
 * Stores the context before calling validation process, so that {@link HbciRestorePreValidationContext} can
 * restore it to the state it was before validation.
 */
@Service("hbciStorePreValidationContext")
public class HbciStorePreValidationContext implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(BEFORE_VALIDATION_CONTEXT, execution.getVariable(CONTEXT));
    }
}
