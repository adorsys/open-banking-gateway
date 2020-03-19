package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Xs2aGetAuthorizationStateViolationsMapperTest.TestConfig.class)
public class Xs2aGetAuthorizationStateViolationsMapperTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/validation_error_set_from_";

    @Autowired
    private Xs2aGetAuthorizationState.ViolationsMapper mapper;

    @Test
    @SneakyThrows
    public void xs2aGetAuthorizationStateViolationsMapperTest() {
        // Given
        Set<ValidationIssue> mappingInput = getFromFile(PATH_PREFIX + "validation_issue_input.json", new TypeReference<Set<ValidationIssue>>() {
        });
        Set<ValidationError> expected = getFromFile(PATH_PREFIX + "validation_issue_output.json", new TypeReference<Set<ValidationError>>() {
        });

        // When
        Set<ValidationError> actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }

    @SneakyThrows
    private <T> T getFromFile(String path, TypeReference<T> valueType) {
        return JSON_MAPPER.readValue(Resources.getResource(path), valueType);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.protocol.xs2a.service.mappers.generated")
    public static class TestConfig {
    }
}