package de.adorsys.opba.protocol.xs2a.domain;

import de.adorsys.opba.protocol.xs2a.constant.GlobalConst;
import lombok.Getter;
import org.flowable.engine.delegate.BpmnError;

/**
 * Special exception that is handled by BPMN engine to capture validation problem.
 */
@Getter
public class ValidationIssueException extends BpmnError {

    public ValidationIssueException() {
        super(GlobalConst.VALIDATION_ERROR_CODE);
    }
}
