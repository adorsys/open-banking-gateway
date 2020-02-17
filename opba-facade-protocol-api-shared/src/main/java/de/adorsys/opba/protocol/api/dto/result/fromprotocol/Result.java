package de.adorsys.opba.protocol.api.dto.result.fromprotocol;

public interface Result<T> {

    default String authContext() {
        return null;
    }

    default T getBody() {
        return null;
    }
}
