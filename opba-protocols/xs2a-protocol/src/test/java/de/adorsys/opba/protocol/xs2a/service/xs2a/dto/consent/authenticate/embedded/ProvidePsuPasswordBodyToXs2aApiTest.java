package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.authenticate.embedded;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ProvidePsuPasswordBodyToXs2aApiTest.TestConfig.class)
public class ProvidePsuPasswordBodyToXs2aApiTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/update_psu_authentication_from_";

    @Autowired
    private ProvidePsuPasswordBody.ToXs2aApi mapper;

    @Test
    @SneakyThrows
    public void providePsuPasswordBodyToXs2aApiMapperTest() {
        // Given
        ProvidePsuPasswordBody mappingInput = getFromFile(PATH_PREFIX + "provide_psu_password_body_input.json", ProvidePsuPasswordBody.class);
        UpdatePsuAuthentication expected = getFromFile(PATH_PREFIX + "provide_psu_password_body_output.json", UpdatePsuAuthentication.class);

        // When
        UpdatePsuAuthentication actual = mapper.map(mappingInput);

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