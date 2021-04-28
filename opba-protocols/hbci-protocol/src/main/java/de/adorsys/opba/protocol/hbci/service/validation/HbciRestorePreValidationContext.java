package de.adorsys.opba.protocol.hbci.service.validation;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.bpmnshared.dto.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.LastViolations;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.BEFORE_VALIDATION_CONTEXT;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.LAST_VALIDATION_ISSUES;

/**
 * Restore the context as it was before validation. As executing validation process changes the context variables
 * with i.e. stub values or intermediate values, this service restores the context to the state it was before
 * validation process was executed.
 */
@RequiredArgsConstructor
@Service("hbciRestorePreValidationContext")
public class HbciRestorePreValidationContext implements JavaDelegate {

    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    public void execute(DelegateExecution execution) {
        HbciContext current = ContextUtil.getContext(execution, HbciContext.class);

        logResolver.log("execute: execution ({}) with context ({})", execution, current);

        execution.setVariable(
            LAST_VALIDATION_ISSUES,
            new LastViolations(current.getViolations(), current.isConsentIncompatible(), current.getRequestScoped())
        );
        execution.setVariable(
            LAST_REDIRECTION_TARGET,
            lastRedirectionTarget(current)
        );
        HbciContext restored = (HbciContext) execution.getVariable(BEFORE_VALIDATION_CONTEXT);
        restored.setConsentIncompatible(current.isConsentIncompatible());

        execution.setVariable(CONTEXT, restored);
        execution.removeVariable(BEFORE_VALIDATION_CONTEXT);

        logResolver.log("done execution ({}) with context ({})", execution, current);
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
