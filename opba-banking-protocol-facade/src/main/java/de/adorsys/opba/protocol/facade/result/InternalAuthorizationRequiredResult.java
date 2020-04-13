package de.adorsys.opba.protocol.facade.result;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;

import java.net.URI;

public class InternalAuthorizationRequiredResult<T, C> extends RedirectionResult<T, C> {

    public InternalAuthorizationRequiredResult() {
        super(URI.create(""), null);
    }
}