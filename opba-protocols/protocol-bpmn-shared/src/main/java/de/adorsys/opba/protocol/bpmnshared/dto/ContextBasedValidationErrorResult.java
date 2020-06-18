package de.adorsys.opba.protocol.bpmnshared.dto;

import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;

import java.net.URI;

/**
 * The result that represents that we need certain fields to be served as required inputs from the user.
 * @param <T>
 */
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
