package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aAuthorizedConsentParametersFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_authorized_consent_parameters_from_";

    @Autowired
    private Xs2aAuthorizedConsentParameters.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aAuthorizedConsentParametersFromCtxMapperTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_consent_input.json", Xs2aContext.class);
        Xs2aAuthorizedConsentParameters expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_consent_output.json", Xs2aAuthorizedConsentParameters.class);

        // When
        Xs2aAuthorizedConsentParameters actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}