package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Xs2aObjectMapper {

    private final ObjectMapper mapper;
}
