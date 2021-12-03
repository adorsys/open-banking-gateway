package de.adorsys.opba.protocol.facade.dto.result.torest;

import java.util.UUID;

/**
 * Facade result to be passed to REST layer
 * @param <T> Response body
 */
public interface FacadeResult<T> {

    /**
     * X-Request-ID of source request associated with this response
     */
    UUID getXRequestId();

    /**
     * Service session ID associated with this response
     */
    String getServiceSessionId();

    /**
     * Response body
     */
    default T getBody() {
        return null;
    }
}
