package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ConsentAcquired;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("xs2aReportToFintechConsentAuthorized")
@RequiredArgsConstructor
public class ReportConsentAuthorizationFinished implements JavaDelegate {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute(DelegateExecution execution) {
        applicationEventPublisher.publishEvent(
            ConsentAcquired.builder()
                .executionId(execution.getId())
                .processId(execution.getRootProcessInstanceId())
                .build()
        );
    }
}
