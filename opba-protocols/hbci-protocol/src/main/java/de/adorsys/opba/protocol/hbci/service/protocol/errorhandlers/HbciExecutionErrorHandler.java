package de.adorsys.opba.protocol.hbci.service.protocol.errorhandlers;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

/**
 * Flowable execution error handler. Triggered on:
 * <ul>
 *     <li>{@link de.adorsys.opba.protocol.hbci.domain.ValidationIssueException}</li>
 * </ul>
 */
@Slf4j
@Service("hbciExecutionErrorHandler")
public class HbciExecutionErrorHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.error("Handling execution error");
    }
}
