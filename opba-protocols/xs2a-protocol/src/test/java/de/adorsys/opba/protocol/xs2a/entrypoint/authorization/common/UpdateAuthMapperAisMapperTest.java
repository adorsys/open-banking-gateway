package de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = UpdateAuthMapperAisMapperTest.TestConfig.class)
public class UpdateAuthMapperAisMapperTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/consent_initiate_body_from_ctx_";

    @Autowired
    private UpdateAuthMapper.AisMapper mapper;

    @Test
    @SneakyThrows
    public void aisMapperTest() {
        // Given
        AisConsent mappingInput = getFromFile(PATH_PREFIX + "ais_consent_input.json", AisConsent.class);
        AisConsentInitiateBody expected = getFromFile(PATH_PREFIX + "ais_consent_output.json", AisConsentInitiateBody.class);

        // When
        AisConsentInitiateBody actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByFieldRecursively(actual);
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