package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aGetAuthorizationStateScaMethodsMapperTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/sca_method_set_from_";

    @Autowired
    private Xs2aGetAuthorizationState.ScaMethodsMapper mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aGetAuthorizationStateScaMethodsMapperTest() {
        // Given
        List<ScaMethod> mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "sca_method_list_input.json", new TypeReference<List<ScaMethod>>() {
        });
        Set<de.adorsys.opba.protocol.api.dto.result.body.ScaMethod> expected = fixtureProvider.getFromFile(PATH_PREFIX + "sca_method_list_output.json", new TypeReference<Set<de.adorsys.opba.protocol.api.dto.result.body.ScaMethod>>() {
        });

        // When
        Set<de.adorsys.opba.protocol.api.dto.result.body.ScaMethod> actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }
}