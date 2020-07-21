package de.adorsys.opba.protocol.hbci.service.validation;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.dto.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ValidationProblem;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.hbci.config.HbciProtocolConfiguration;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.LastViolations;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.LAST_VALIDATION_ISSUES;
/**
 * Reports that there were validation errors on context. As the result user should be redirected to the form,
 * where he can provide missing data to the context to proceed with authorization. Typically that data is passed
 * back to the context using {@link de.adorsys.opba.protocol.api.authorization.UpdateAuthorization}.
 */
@RequiredArgsConstructor
@Service("hbciReportValidationError")
public class HbciReportValidationError implements JavaDelegate {

    private final HbciProtocolConfiguration configuration;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(DelegateExecution execution) {
        // Make transient context with all violations for clear mapping
        HbciContext current = ContextUtil.getContext(execution, HbciContext.class);
        LastViolations violations = execution.getVariable(LAST_VALIDATION_ISSUES, LastViolations.class);
        LastRedirectionTarget redirectionTarget = execution.getVariable(LAST_REDIRECTION_TARGET, LastRedirectionTarget.class);
        current.setLastRedirection(redirectionTarget);
        current.setViolations(violations.getViolations());

        HbciProtocolConfiguration.UrlSet urlSet = ProtocolAction.SINGLE_PAYMENT.equals(current.getAction())
                ? configuration.getPis() : configuration.getAis();

        eventPublisher.publishEvent(
                ValidationProblem.builder()
                        .processId(current.getSagaId())
                        .executionId(execution.getId())
                        .consentIncompatible(violations.isConsentIncompatible())
                        .provideMoreParamsDialog(
                                ContextUtil.evaluateSpelForCtx(
                                        urlSet.getRedirect().getParameters().getProvideMore(),
                                        execution,
                                        current,
                                        URI.class)
                        )
                        .issues(current.getViolations())
                        .build()
        );
    }
}
