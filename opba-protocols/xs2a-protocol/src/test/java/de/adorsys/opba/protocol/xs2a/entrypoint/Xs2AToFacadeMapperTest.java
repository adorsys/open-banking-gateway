package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.context.Context;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2AToFacadeMapperTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_to_facade_response_mapper_";

    @Autowired
    private Xs2aResultBodyExtractor.Xs2aToFacadeMapper mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    void accountsMapperTest() {
        AccountList mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "accounts_input.json", AccountList.class);
        AccountListBody mappingResult = mapper.map(mappingInput);

        AccountListBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "accounts_output.json", AccountListBody.class);
        assertThat(expected).isEqualToComparingFieldByField(mappingResult);
    }

    @Test
    @SneakyThrows
    void transactionsMapperTest() {
        TransactionsResponse200Json mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "transactions_input.json",
                TransactionsResponse200Json.class);

        ListTransactionsRequest listTransactionsRequest = ListTransactionsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .build()
                )
                .page(1)
                .perPage(500)
                .build();

        ServiceContext<ListTransactionsRequest> context = ServiceContext.<ListTransactionsRequest>builder()
                .ctx(Context.<ListTransactionsRequest>builder()
                        .serviceSessionId(UUID.randomUUID())
                        .request(listTransactionsRequest)
                        .build())
                .build();

        TransactionsResponseBody mappingResult = mapper.map(mappingInput, context);

        TransactionsResponseBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "transactions_output.json",
                                                                        TransactionsResponseBody.class);
        assertThat(expected).isEqualToComparingFieldByField(mappingResult);
    }
}
