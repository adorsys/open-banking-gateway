package de.adorsys.opba.core.protocol.domain;

import lombok.Getter;
import org.flowable.engine.delegate.BpmnError;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.VALIDATION_ERROR_CODE;

@Getter
public class ValidationIssueException extends BpmnError {

    public ValidationIssueException() {
        super(VALIDATION_ERROR_CODE);
    }
}
