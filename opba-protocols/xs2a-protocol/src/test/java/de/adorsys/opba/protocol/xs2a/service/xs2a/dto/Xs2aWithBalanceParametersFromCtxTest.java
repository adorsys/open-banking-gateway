package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aWithBalanceParametersFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_with_balance_parameters_from_";

    @Autowired
    private Xs2aWithBalanceParameters.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aWithBalanceParametersFromCtxMapperTest() {
        // Given
        Xs2aAisContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_consent_input.json", Xs2aAisContext.class);
        Xs2aWithBalanceParameters expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_consent_output.json", Xs2aWithBalanceParameters.class);

        // When
        Xs2aWithBalanceParameters actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}