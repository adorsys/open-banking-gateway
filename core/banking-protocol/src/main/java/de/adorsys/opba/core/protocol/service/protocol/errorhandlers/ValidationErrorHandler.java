package de.adorsys.opba.core.protocol.service.protocol.errorhandlers;

import de.adorsys.opba.core.protocol.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.core.protocol.domain.dto.messages.ValidationIssueResult;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URI;

@Slf4j
@Service("handleAndClearValidationErrors")
@RequiredArgsConstructor
public class ValidationErrorHandler implements JavaDelegate {

    private final ProtocolConfiguration configuration;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(DelegateExecution execution) {
        BaseContext ctx = ContextUtil.getContext(execution, BaseContext.class);
        eventPublisher.publishEvent(
                ValidationIssueResult.builder()
                        .processId(ctx.getSagaId())
                        .provideMoreParamsDialog(
                                ContextUtil.evaluateSpelForCtx(
                                        configuration.getRedirect().getParameters().getProvideMore(),
                                        execution,
                                        ctx,
                                        URI.class)
                        )
                        .build()
        );

        ctx.getViolations().clear();
        ContextUtil.updateContext(execution, ctx);
    }
}
