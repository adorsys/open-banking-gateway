package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Wrapper for {@link ObjectMapper} to clearly identify which container is used.
 */
@Getter
@RequiredArgsConstructor
public class Xs2aObjectMapper {

    @Delegate
    private final ObjectMapper mapper;
}
