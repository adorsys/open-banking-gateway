package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aInitialConsentParametersFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_initial_consent_parameters_from_";

    @Autowired
    private Xs2aInitialConsentParameters.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aInitialConsentParametersFromCtxMapperTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_input.json", Xs2aContext.class);
        Xs2aInitialConsentParameters expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_output.json", Xs2aInitialConsentParameters.class);

        // When
        Xs2aInitialConsentParameters actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}