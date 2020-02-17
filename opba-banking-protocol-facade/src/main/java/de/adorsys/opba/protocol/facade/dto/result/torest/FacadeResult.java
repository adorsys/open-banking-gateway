package de.adorsys.opba.protocol.facade.dto.result.torest;

import java.util.UUID;

public interface FacadeResult<T> {

    UUID getXRequestId();
    String getServiceSessionId();

    default T getBody() {
        return null;
    }
}
