package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

/**
 * Protocol result that says certain inputs are required from user (i.e. PSU_ID).
 */
public class ValidationErrorResult<T, C> extends RedirectionResult<T, C> {

    public ValidationErrorResult(URI redirectionTo, C body) {
        super(redirectionTo, body);
    }
}
