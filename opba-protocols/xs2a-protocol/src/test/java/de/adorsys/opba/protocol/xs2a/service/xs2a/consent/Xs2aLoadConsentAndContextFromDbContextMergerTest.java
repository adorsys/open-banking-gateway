package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aLoadConsentAndContextFromDbContextMergerTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/context_merger_";

    @Autowired
    private Xs2aLoadConsentAndContextFromDb.ContextMerger mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aContextMapperTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_to_xs2a_input.json", Xs2aContext.class);
        Xs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_to_xs2a_output.json", Xs2aContext.class);
        Xs2aContext actual = new Xs2aContext();

        // When
        mapper.merge(mappingInput, actual);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    @SneakyThrows
    public void xs2sToTransactionListXs2aContextMapperTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_to_transaction_list_input.json", Xs2aContext.class);
        TransactionListXs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_to_transaction_list_output.json", TransactionListXs2aContext.class);
        TransactionListXs2aContext actual = new TransactionListXs2aContext();

        // When
        mapper.merge(mappingInput, actual);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    @SneakyThrows
    public void transactionListXs2aContextMapperTest() {
        // Given
        TransactionListXs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "transaction_to_transaction_list_input.json", TransactionListXs2aContext.class);
        TransactionListXs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "transaction_to_transaction_list_output.json", TransactionListXs2aContext.class);
        TransactionListXs2aContext actual = new TransactionListXs2aContext();

        // When
        mapper.merge(mappingInput, actual);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    @SneakyThrows
    public void accountListXs2aContextMapperTest() {
        // Given
        AccountListXs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "account_to_transaction_list_input.json", AccountListXs2aContext.class);
        TransactionListXs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "account_to_transaction_list_output.json", TransactionListXs2aContext.class);
        TransactionListXs2aContext actual = new TransactionListXs2aContext();

        // When
        mapper.merge(mappingInput, actual);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}