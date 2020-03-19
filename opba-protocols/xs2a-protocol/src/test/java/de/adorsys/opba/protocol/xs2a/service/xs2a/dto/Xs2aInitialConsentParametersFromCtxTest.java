package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Xs2aInitialConsentParametersFromCtxTest.TestConfig.class)
public class Xs2aInitialConsentParametersFromCtxTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_initial_consent_parameters_from_";

    @Autowired
    private Xs2aInitialConsentParameters.FromCtx mapper;

    @Test
    @SneakyThrows
    public void xs2aInitialConsentParametersFromCtxMapperTest() {
        // Given
        Xs2aContext mappingInput = getFromFile(PATH_PREFIX + "xs2a_context_input.json", Xs2aContext.class);
        Xs2aInitialConsentParameters expected = getFromFile(PATH_PREFIX + "xs2a_context_output.json", Xs2aInitialConsentParameters.class);

        // When
        Xs2aInitialConsentParameters actual = mapper.map(mappingInput);

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