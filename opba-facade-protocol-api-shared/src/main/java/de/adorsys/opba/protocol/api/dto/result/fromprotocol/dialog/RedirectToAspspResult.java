package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

/**
 * Indicates the redirection to ASPSP.
 */
public class RedirectToAspspResult<T> extends AuthorizationRequiredResult<T, Object> {
    private final String executionId;

    public RedirectToAspspResult(URI redirectionTo, String executionId) {
        super(redirectionTo, null);
        this.executionId = executionId;
    }

    @Override
    public String authContext() {
        return executionId;
    }
}
