package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("xs2aReportToFintechConsentAuthorized")
@RequiredArgsConstructor
public class ReportConsentAuthorizationFinished implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {

    }
}
