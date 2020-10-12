package de.adorsys.opba.protocol.xs2a.service.validation;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.bpmnshared.dto.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ValidationProblem;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.LastViolations;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_VALIDATION_ISSUES;

/**
 * Reports that there were validation errors on context. As the result user should be redirected to the form,
 * where he can provide missing data to the context to proceed with authorization. Typically that data is passed
 * back to the context using {@link de.adorsys.opba.protocol.api.authorization.UpdateAuthorization}.
 */
@RequiredArgsConstructor
@Service("xs2aReportValidationError")
public class Xs2aReportValidationError implements JavaDelegate {

    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(DelegateExecution execution) {
        // Make transient context with all violations for clear mapping
        BaseContext current = ContextUtil.getContext(execution, BaseContext.class);
        LastViolations violations = execution.getVariable(LAST_VALIDATION_ISSUES, LastViolations.class);
        LastRedirectionTarget redirectionTarget = execution.getVariable(LAST_REDIRECTION_TARGET, LastRedirectionTarget.class);
        current.setLastRedirection(redirectionTarget);
        current.setViolations(violations.getViolations());

        ProtocolUrlsConfiguration.UrlSet urlSet = ProtocolAction.SINGLE_PAYMENT.equals(current.getAction())
                ? urlsConfiguration.getPis() : urlsConfiguration.getAis();

        eventPublisher.publishEvent(
                ValidationProblem.builder()
                        .processId(current.getSagaId())
                        .executionId(execution.getId())
                        .provideMoreParamsDialog(
                                UriComponentsBuilder.fromHttpUrl(urlSet.getParameters().getProvideMore())
                                        .queryParam("redirectCode", current.getRedirectCodeIfAuthContinued())
                                        .buildAndExpand(ImmutableMap.of("sessionId", current.getAuthorizationSessionIdIfOpened()))
                                        .toUri()
                        )
                        .issues(current.getViolations())
                        .build()
        );
    }
}
