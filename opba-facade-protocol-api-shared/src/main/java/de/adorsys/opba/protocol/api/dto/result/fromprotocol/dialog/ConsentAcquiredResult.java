package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

public class ConsentAcquiredResult<T> extends RedirectionResult<T> {

    public ConsentAcquiredResult(URI redirectionTo) {
        super(redirectionTo);
    }
}
