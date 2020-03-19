package de.adorsys.opba.protocol.xs2a.domain.dto.forms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.xs2a.adapter.service.model.AuthenticationObject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FromAuthObjectTest.TestConfig.class)
public class FromAuthObjectTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/sca_method_from_auth_object_";

    private ScaMethod.FromAuthObject mapper = Mappers.getMapper(ScaMethod.FromAuthObject.class);

    @SneakyThrows
    private <T> T getFromFile(String path, Class<T> valueType) {
        return JSON_MAPPER.readValue(Resources.getResource(path), valueType);
    }

    @Test
    @SneakyThrows
    public void scaMethodMapperTest() {
        // Given
        AuthenticationObject mappingInput = getFromFile(PATH_PREFIX + "sca_method_input.json", AuthenticationObject.class);
        ScaMethod expected = getFromFile(PATH_PREFIX + "sca_method_output.json", ScaMethod.class);

        // When
        ScaMethod actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.protocol.xs2a.domain.dto.forms")
    public static class TestConfig {
    }
}