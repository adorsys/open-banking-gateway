package de.adorsys.opba.protocol.xs2a.domain;

import de.adorsys.opba.protocol.xs2a.constant.GlobalConst;
import lombok.Getter;
import org.flowable.engine.delegate.BpmnError;

/**
 * Special exception that is handled by BPMN engine to capture validation problem.
 */
@Getter
public class ValidationIssueException extends BpmnError {

    private final String message;

    public ValidationIssueException() {
        this(null);
    }

    public ValidationIssueException(String message) {
        super(GlobalConst.VALIDATION_ERROR_CODE);
        this.message = message;
    }
}
