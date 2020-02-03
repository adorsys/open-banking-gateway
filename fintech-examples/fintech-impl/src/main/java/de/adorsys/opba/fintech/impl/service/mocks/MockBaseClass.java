package de.adorsys.opba.fintech.impl.service.mocks;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
public class MockBaseClass {
    static final Gson GSON = new Gson();
    @SneakyThrows
    String readFile(String fileName) {
        String mockValue = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8);
        log.info("MOCK DATA " + mockValue);
        return mockValue;
    }

}
