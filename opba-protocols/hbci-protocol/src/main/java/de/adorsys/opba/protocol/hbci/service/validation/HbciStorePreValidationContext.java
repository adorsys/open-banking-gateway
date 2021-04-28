package de.adorsys.opba.protocol.hbci.service.validation;

import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
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

    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    public void execute(DelegateExecution execution) {
        logResolver.log("execute: execution ({})", execution);

        execution.setVariable(BEFORE_VALIDATION_CONTEXT, execution.getVariable(CONTEXT));

        logResolver.log("done execution ({})", execution);
    }
}
