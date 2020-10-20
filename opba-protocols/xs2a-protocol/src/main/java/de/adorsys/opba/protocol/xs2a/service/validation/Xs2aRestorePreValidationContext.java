package de.adorsys.opba.protocol.xs2a.service.validation;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.bpmnshared.dto.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.context.LastViolations;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.BEFORE_VALIDATION_CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_VALIDATION_ISSUES;

/**
 * Restore the context as it was before validation. As executing validation process changes the context variables
 * with i.e. stub values or intermediate values, this service restores the context to the state it was before
 * validation process was executed.
 */
@RequiredArgsConstructor
@Service("xs2aRestorePreValidationContext")
public class Xs2aRestorePreValidationContext implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        BaseContext current = ContextUtil.getContext(execution, BaseContext.class);
        execution.setVariable(
                LAST_VALIDATION_ISSUES,
                new LastViolations(current.getViolations(), current.getRequestScoped())
        );
        execution.setVariable(
                LAST_REDIRECTION_TARGET,
                lastRedirectionTarget(current)
        );
        execution.setVariable(CONTEXT, execution.getVariable(BEFORE_VALIDATION_CONTEXT));
        execution.removeVariable(BEFORE_VALIDATION_CONTEXT);
    }

    // FIXME SerializerUtil does not support nestedness
    private LastRedirectionTarget lastRedirectionTarget(BaseContext current) {
        if (null == current.getLastRedirection()) {
            return null;
        }

        LastRedirectionTarget target = current.getLastRedirection();
        target.setRequestScoped(current.getRequestScoped());
        return target;
    }
}
