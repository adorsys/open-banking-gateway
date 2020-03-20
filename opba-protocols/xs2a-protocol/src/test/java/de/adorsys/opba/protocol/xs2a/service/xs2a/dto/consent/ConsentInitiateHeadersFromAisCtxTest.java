package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.Xs2aAisContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ConsentInitiateHeadersFromAisCtxTest.TestConfig.class)
public class ConsentInitiateHeadersFromAisCtxTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/consent_initiate_headers_from_";

    @Autowired
    private ConsentInitiateHeaders.FromAisCtx mapper;

    @Test
    @SneakyThrows
    public void consentInitiateHeadersFromAisCtxMapperTest() {
        // Given
        Xs2aAisContext mappingInput = getFromFile(PATH_PREFIX + "xs2a_ais_consent_input.json", Xs2aAisContext.class);
        ConsentInitiateHeaders expected = getFromFile(PATH_PREFIX + "xs2a_ais_consent_output.json", ConsentInitiateHeaders.class);

        // When
        ConsentInitiateHeaders actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }

    @SneakyThrows
    private <T> T getFromFile(String path, Class<T> valueType) {
        return JSON_MAPPER.readValue(Resources.getResource(path), valueType);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.protocol.xs2a.service.mappers.generated")
    public static class TestConfig {
    }
}