package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

/**
 * Indicates that the current consent is incompatible with the request. Authorization is necessary.
 */
public class ConsentIncompatibleResult<T, C> extends ValidationErrorResult<T, C> {

    public ConsentIncompatibleResult(URI redirectionTo, C cause) {
        super(redirectionTo, cause);
    }
}
