package de.adorsys.opba.protocol.xs2a.entrypoint.dto;

import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;

import java.net.URI;

public class ContextBasedValidationErrorResult<T> extends ValidationErrorResult<T, AuthStateBody> {

    private final String executionId;

    public ContextBasedValidationErrorResult(URI redirectionTo, String executionId, AuthStateBody authState) {
        super(redirectionTo, authState);
        this.executionId = executionId;
    }

    @Override
    public String authContext() {
        return executionId;
    }
}
