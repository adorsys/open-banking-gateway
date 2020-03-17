package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.Xs2aAisContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AisConsentInitiateBodyFromCtxTest.TestConfig.class)
public class AisConsentInitiateBodyFromCtxTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/consent_body_from_ctx_";

    @Autowired
    private AisConsentInitiateBody.FromCtx mapper;

    @Test
    @SneakyThrows
    public void aisConsentInitiateBodyMapperTest_non_empty_consent() {
        // Given
        Xs2aAisContext mappingInput = getFromFile(PATH_PREFIX + "ais_consent_non_empty_body_input.json", AccountListXs2aContext.class);
        AisConsentInitiateBody expected = getFromFile(PATH_PREFIX + "ais_consent_non_empty_body_output.json", AisConsentInitiateBody.class);

        // When
        AisConsentInitiateBody actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }

    @Test
    @SneakyThrows
    public void aisConsentInitiateBodyMapperTest_empty_consent() {
        // Given
        Xs2aAisContext mappingInput = getFromFile(PATH_PREFIX + "ais_consent_empty_body_input.json", AccountListXs2aContext.class);
        AisConsentInitiateBody expected = getFromFile(PATH_PREFIX + "ais_consent_empty_body_output.json", AisConsentInitiateBody.class);

        // When
        AisConsentInitiateBody actual = mapper.map(mappingInput);

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