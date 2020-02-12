package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
public class Xs2aObjectMapper {

    @Delegate
    private final ObjectMapper mapper;
}
