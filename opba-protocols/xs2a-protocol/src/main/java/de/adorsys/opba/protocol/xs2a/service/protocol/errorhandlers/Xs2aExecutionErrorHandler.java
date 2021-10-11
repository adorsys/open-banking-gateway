package de.adorsys.opba.protocol.xs2a.service.protocol.errorhandlers;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

/**
 * Flowable execution error handler. Triggered on:
 * <ul>
 *     <li>{@link de.adorsys.opba.protocol.xs2a.domain.ValidationIssueException}</li>
 * </ul>
 */
@Slf4j
@Service("xs2ExecutionErrorHandler")
public class Xs2aExecutionErrorHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.error("Handling execution error");
        throw new IllegalStateException("Should not be reached: " + execution.getId());
    }
}
