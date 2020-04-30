package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aResourceParametersFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_resource_parameters_from_";

    @Autowired
    private Xs2aResourceParameters.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aResourceParametersFromCtxMapperTest() {
        // Given
        TransactionListXs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "transaction_list_context_input.json", TransactionListXs2aContext.class);
        Xs2aResourceParameters expected = fixtureProvider.getFromFile(PATH_PREFIX + "transaction_list_context_output.json", Xs2aResourceParameters.class);

        // When
        Xs2aResourceParameters actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}