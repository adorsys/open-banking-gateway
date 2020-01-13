package de.adorsys.opba.core.protocol.service.protocol.errorhandlers;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Slf4j
@Service("executionErrorHandler")
public class ExecutionErrorHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.error("Handling execution error");
    }
}
