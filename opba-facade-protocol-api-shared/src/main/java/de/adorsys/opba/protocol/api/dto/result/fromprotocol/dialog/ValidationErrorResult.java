package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

public class ValidationErrorResult<T, C> extends RedirectionResult<T, C> {

    public ValidationErrorResult(URI redirectionTo, C body) {
        super(redirectionTo, body);
    }
}
