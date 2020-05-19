package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ValidationProblem;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.config.HbciProtocolConfiguration;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * Initiates Account list consent. Forcefully redirects user to consent initiation screen.
 */
@Slf4j
@Service("hbciAisConsentInitiate")
@RequiredArgsConstructor
public class HbciAisConsentService extends ValidatedExecution<AccountListHbciContext> {

    private final ApplicationEventPublisher eventPublisher;
    private final HbciProtocolConfiguration configuration;

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        eventPublisher.publishEvent(ValidationProblem.builder()
                .processId(context.getSagaId())
                .executionId(execution.getId())
                .provideMoreParamsDialog(
                        ContextUtil.evaluateSpelForCtx(
                                configuration.getRedirect().getParameters().getProvideMore(),
                                execution,
                                context,
                                URI.class)
                )
                .build());
    }
}
