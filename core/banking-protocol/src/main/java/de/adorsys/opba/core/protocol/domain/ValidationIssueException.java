package de.adorsys.opba.core.protocol.domain;

import lombok.Getter;
import org.flowable.engine.delegate.BpmnError;

@Getter
public class ValidationIssueException extends BpmnError {

    public ValidationIssueException() {
        super("VALIDATION");
    }
}
