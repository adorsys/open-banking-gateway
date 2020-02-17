package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("xs2aReportToFintechConsentAuthorized")
@RequiredArgsConstructor
public class ReportConsentAuthorizationFinished extends ValidatedExecution<Xs2aContext> {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        applicationEventPublisher.publishEvent(
            ConsentAcquired.builder()
                .executionId(execution.getId())
                .processId(execution.getRootProcessInstanceId())
                .build()
        );
    }
}
