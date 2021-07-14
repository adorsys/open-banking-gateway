package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.opba.protocol.api.dto.parameters.ExtraRequestParam;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aListTransactionsEntrypointFromRequestTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/transaction_list_xs2a_context_from_";

    @Autowired
    private Xs2aListTransactionsEntrypoint.FromRequest mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aListTransactionsEntrypointFromRequestMapperTest() {
        // Given
        ListTransactionsRequest mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "list_transactions_request_input.json", ListTransactionsRequest.class);
        TransactionListXs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "list_transactions_request_output.json", TransactionListXs2aContext.class);

        // When
        TransactionListXs2aContext actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    @SneakyThrows
    public void xs2aListTransactionsEntrypointFromRequestMapperWithConsentInExtrasTest() {
        // Given
        ListTransactionsRequest mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "list_transactions_request_with_extras_input.json", ListTransactionsRequest.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        AisConsent aisConsent = objectMapper.readValue((String) mappingInput.getExtras().get(ExtraRequestParam.CONSENT), AisConsent.class);
        mappingInput.getExtras().put(ExtraRequestParam.CONSENT,  aisConsent);
        TransactionListXs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "list_transactions_request_with_extras_output.json", TransactionListXs2aContext.class);

        // When
        TransactionListXs2aContext actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}