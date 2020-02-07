package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

public class AuthorizationRequiredResult<T> extends RedirectionResult<T> {

    public AuthorizationRequiredResult(URI redirectionTo) {
        super(redirectionTo);
    }
}
