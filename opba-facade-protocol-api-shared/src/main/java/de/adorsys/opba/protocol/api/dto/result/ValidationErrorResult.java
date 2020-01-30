package de.adorsys.opba.protocol.api.dto.result;

import java.net.URI;

public class ValidationErrorResult<T> extends RedirectionResult<T> {

    public ValidationErrorResult(URI redirectionTo) {
        super(redirectionTo);
    }
}
