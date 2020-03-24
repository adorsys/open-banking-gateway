package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aGetAuthorizationStateViolationsMapperTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/validation_error_set_from_";

    @Autowired
    private Xs2aGetAuthorizationState.ViolationsMapper mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aGetAuthorizationStateViolationsMapperTest() {
        // Given
        Set<ValidationIssue> mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "validation_issue_input.json", new TypeReference<Set<ValidationIssue>>() {
        });
        Set<ValidationError> expected = fixtureProvider.getFromFile(PATH_PREFIX + "validation_issue_output.json", new TypeReference<Set<ValidationError>>() {
        });

        // When
        Set<ValidationError> actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }
}