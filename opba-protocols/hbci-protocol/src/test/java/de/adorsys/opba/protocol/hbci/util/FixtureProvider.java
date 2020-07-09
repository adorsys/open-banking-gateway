package de.adorsys.opba.protocol.hbci.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FixtureProvider {
    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    public <T> T getFromFile(String path, Class<T> valueType) {
        return mapper.readValue(Resources.getResource(path), valueType);
    }

    @SneakyThrows
    public <T> T getFromFile(String path, TypeReference<T> valueType) {
        return mapper.readValue(Resources.getResource(path), valueType);
    }
}
