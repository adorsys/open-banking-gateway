package de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common;

import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class UpdateAuthMapperFromAisRequestAccountListTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/account_list_context_from_ctx_";

    @Autowired
    private UpdateAuthMapper.FromAisRequestAccountList mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void fromAisRequestAccountListMapperTest() {
        // Given
        AuthorizationRequest mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "authorization_request_input.json", AuthorizationRequest.class);
        AccountListXs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "authorization_request_output.json", AccountListXs2aContext.class);
        AccountListXs2aContext actual = new AccountListXs2aContext();

        // When
        mapper.map(mappingInput, actual);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}