package de.adorsys.opba.protocol.api.dto.result;

import java.util.Optional;

public interface Result<T> {

    default String authContext() {
        return null;
    }

    default Optional<T> result() {
        return Optional.empty();
    }
}
