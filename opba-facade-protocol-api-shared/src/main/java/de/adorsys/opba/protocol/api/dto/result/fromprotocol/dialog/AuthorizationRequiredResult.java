package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

/**
 * Indicates that the operation requested requires (consent) authorization.
 */
public class AuthorizationRequiredResult<T, C> extends RedirectionResult<T, C> {

    public AuthorizationRequiredResult(URI redirectionTo, C cause) {
        super(redirectionTo, cause);
    }
}
