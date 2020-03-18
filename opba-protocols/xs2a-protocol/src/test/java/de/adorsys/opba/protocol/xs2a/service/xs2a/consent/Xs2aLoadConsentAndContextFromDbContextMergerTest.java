package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.TransactionListXs2aContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Xs2aLoadConsentAndContextFromDbContextMergerTest.TestConfig.class)
public class Xs2aLoadConsentAndContextFromDbContextMergerTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/context_merger_";

    @Autowired
    private Xs2aLoadConsentAndContextFromDb.ContextMerger mapper;

    @Test
    @SneakyThrows
    public void xs2aContextMapperTest() {
        // Given
        Xs2aContext mappingInput = getFromFile(PATH_PREFIX + "xs2a_to_xs2a_input.json", Xs2aContext.class);
        Xs2aContext expected = getFromFile(PATH_PREFIX + "xs2a_to_xs2a_output.json", Xs2aContext.class);
        Xs2aContext actual = new Xs2aContext();

        // When
        mapper.merge(mappingInput, actual);

        // Then
        assertThat(expected).isEqualToComparingFieldByFieldRecursively(actual);
    }

    @Test
    @SneakyThrows
    public void xs2sToTransactionListXs2aContextMapperTest() {
        // Given
        Xs2aContext mappingInput = getFromFile(PATH_PREFIX + "xs2a_to_transaction_list_input.json", Xs2aContext.class);
        TransactionListXs2aContext expected = getFromFile(PATH_PREFIX + "xs2a_to_transaction_list_output.json", TransactionListXs2aContext.class);
        TransactionListXs2aContext actual = new TransactionListXs2aContext();

        // When
        mapper.merge(mappingInput, actual);

        // Then
        assertThat(expected).isEqualToComparingFieldByFieldRecursively(actual);
    }

    @Test
    @SneakyThrows
    public void transactionListXs2aContextMapperTest() {
        // Given
        TransactionListXs2aContext mappingInput = getFromFile(PATH_PREFIX + "transaction_to_transaction_list_input.json", TransactionListXs2aContext.class);
        TransactionListXs2aContext expected = getFromFile(PATH_PREFIX + "transaction_to_transaction_list_output.json", TransactionListXs2aContext.class);
        TransactionListXs2aContext actual = new TransactionListXs2aContext();

        // When
        mapper.merge(mappingInput, actual);

        // Then
        assertThat(expected).isEqualToComparingFieldByFieldRecursively(actual);
    }

    @Test
    @SneakyThrows
    public void accountListXs2aContextMapperTest() {
        // Given
        AccountListXs2aContext mappingInput = getFromFile(PATH_PREFIX + "account_to_transaction_list_input.json", AccountListXs2aContext.class);
        TransactionListXs2aContext expected = getFromFile(PATH_PREFIX + "account_to_transaction_list_output.json", TransactionListXs2aContext.class);
        TransactionListXs2aContext actual = new TransactionListXs2aContext();

        // When
        mapper.merge(mappingInput, actual);

        // Then
        assertThat(expected).isEqualToComparingFieldByFieldRecursively(actual);
    }

    @SneakyThrows
    private <T> T getFromFile(String path, Class<T> valueType) {
        return JSON_MAPPER.readValue(Resources.getResource(path), valueType);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.protocol.xs2a.service.mappers.generated")
    public static class TestConfig {
    }
}