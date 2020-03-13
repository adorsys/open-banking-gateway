package de.adorsys.opba.protocol.xs2a.service.validation;

import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.LastViolations;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.BEFORE_VALIDATION_CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_VALIDATION_ISSUES;

@Service("xs2aRestorePreValidationContext")
public class Xs2aRestorePreValidationContext implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(
            LAST_VALIDATION_ISSUES,
            new LastViolations(ContextUtil.getContext(execution, BaseContext.class).getViolations())
        );
        execution.setVariable(
            LAST_REDIRECTION_TARGET,
            ContextUtil.getContext(execution, BaseContext.class).getRedirectTo()
        );
        execution.setVariable(CONTEXT, execution.getVariable(BEFORE_VALIDATION_CONTEXT));
        execution.removeVariable(BEFORE_VALIDATION_CONTEXT);
    }
}
