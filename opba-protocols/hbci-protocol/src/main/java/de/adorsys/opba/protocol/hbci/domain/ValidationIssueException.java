package de.adorsys.opba.protocol.hbci.domain;

import de.adorsys.opba.protocol.hbci.constant.GlobalConst;
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
