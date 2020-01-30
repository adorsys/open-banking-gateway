package de.adorsys.opba.protocol.api.dto.result;

import java.util.Optional;

public interface Result<T> {

    default Optional<T> result() {
        return Optional.empty();
    }
}
