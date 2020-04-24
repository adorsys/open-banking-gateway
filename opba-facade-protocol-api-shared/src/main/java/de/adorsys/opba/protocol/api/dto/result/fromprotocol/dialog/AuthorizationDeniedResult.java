package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

/**
 * Indicates that the authorization was denied.
 */
public class AuthorizationDeniedResult<T, C> extends RedirectionResult<T, C> {

    public AuthorizationDeniedResult(URI redirectionTo, C cause) {
        super(redirectionTo, cause);
    }
}
