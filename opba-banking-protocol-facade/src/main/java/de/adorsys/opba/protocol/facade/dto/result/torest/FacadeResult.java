package de.adorsys.opba.protocol.facade.dto.result.torest;

public interface FacadeResult<T> {

    default T getBody() {
        return null;
    }
}
