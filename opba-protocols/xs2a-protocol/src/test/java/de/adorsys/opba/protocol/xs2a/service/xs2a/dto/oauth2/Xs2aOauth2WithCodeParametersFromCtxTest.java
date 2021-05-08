package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import de.adorsys.xs2a.adapter.api.Oauth2Service;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
class Xs2aOauth2WithCodeParametersFromCtxTest {

    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_oauth2_with_code_parameters_from_";

    @Autowired
    private Xs2aOauth2WithCodeParameters.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aOauth2WithCodeParametersFromCtxTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_input.json", Xs2aContext.class);
        Xs2aOauth2WithCodeParameters expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_output.json", Xs2aOauth2WithCodeParameters.class);

        // When
        Xs2aOauth2WithCodeParameters actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }

    @Test
    @SneakyThrows
    public void xs2aOauth2WithCodeParametersFromCtxToParametersTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_input.json", Xs2aContext.class);
        var expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_to_parameters_output.json", Oauth2Service.Parameters.class);

        // When
        var actual = mapper.map(mappingInput).toParameters();

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}