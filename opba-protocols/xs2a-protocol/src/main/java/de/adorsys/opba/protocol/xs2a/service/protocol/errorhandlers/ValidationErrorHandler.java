package de.adorsys.opba.protocol.xs2a.service.protocol.errorhandlers;

import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ValidationIssue;
import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URI;

@Slf4j
@Service("handleValidationErrors")
@RequiredArgsConstructor
public class ValidationErrorHandler implements JavaDelegate {

    private final ProtocolConfiguration configuration;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(DelegateExecution execution) {
        BaseContext ctx = ContextUtil.getContext(execution, BaseContext.class);
        eventPublisher.publishEvent(
                ValidationIssue.builder()
                        .processId(ctx.getSagaId())
                        .executionId(execution.getId())
                        .provideMoreParamsDialog(
                                ContextUtil.evaluateSpelForCtx(
                                        configuration.getRedirect().getParameters().getProvideMore(),
                                        execution,
                                        ctx,
                                        URI.class)
                        )
                        .build()
        );
    }
}
