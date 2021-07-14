package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.opba.protocol.api.dto.parameters.ExtraRequestParam;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
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

    @Test
    @SneakyThrows
    public void listAccountMapperWithConsentInExtrasTest() {
        // Given
        ListAccountsRequest mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "list_account_request_with_extras_input.json", ListAccountsRequest.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        AisConsent aisConsent = objectMapper.readValue((String) mappingInput.getExtras().get(ExtraRequestParam.CONSENT), AisConsent.class);
        mappingInput.getExtras().put(ExtraRequestParam.CONSENT,  aisConsent);
        AccountListXs2aContext expected = fixtureProvider.getFromFile(PATH_PREFIX + "list_account_request_with_extras_output.json", AccountListXs2aContext.class);

        // When
        AccountListXs2aContext actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}
