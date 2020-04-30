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
public class Xs2aTransactionParametersFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/transaction_parameters_from_ctx_";

    @Autowired
    private Xs2aTransactionParameters.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void transactionParametersMapperTest() {
        // Given
        TransactionListXs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "transaction_list_input.json", TransactionListXs2aContext.class);
        Xs2aTransactionParameters expected = fixtureProvider.getFromFile(PATH_PREFIX + "transaction_list_output.json", Xs2aTransactionParameters.class);

        // When
        Xs2aTransactionParameters actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}