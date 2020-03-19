package de.adorsys.opba.protocol.xs2a.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class UtilService {
    private final ObjectMapper mapper = new ObjectMapper()
                                                .registerModule(new JavaTimeModule())
                                                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @SneakyThrows
    public <T> T getFromFile(String path, Class<T> valueType) {
        return mapper.readValue(Resources.getResource(path), valueType);
    }

    @SneakyThrows
    public <T> T getFromFile(String path, TypeReference<T> valueType) {
        return mapper.readValue(Resources.getResource(path), valueType);
    }
}
