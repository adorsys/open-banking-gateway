package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aListAccountsEntrypointFromRequestTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/account_list_context_from_ctx_";

    @Autowired
    private Xs2aListAccountsEntrypoint.FromRequest mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void listAccountMapperTest() {
        // Given
        ListAccountsRequest mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "list_account_request_input.json", ListAccountsRequest.class);
        AccountListXs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "list_account_request_output.json", AccountListXs2aContext.class);

        // When
        AccountListXs2aContext actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}