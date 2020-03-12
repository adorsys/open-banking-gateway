package de.adorsys.opba.protocol.xs2a.entrypoint.dto;

import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;

import java.net.URI;
import java.util.Set;

public class ContextBasedValidationErrorResult<T> extends ValidationErrorResult<T, Set<ValidationIssue>> {

    private final String executionId;

    public ContextBasedValidationErrorResult(URI redirectionTo, String executionId, Set<ValidationIssue> issues) {
        super(redirectionTo, issues);
        this.executionId = executionId;
    }

    @Override
    public String authContext() {
        return executionId;
    }
}
