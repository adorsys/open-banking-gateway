package de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.AccountListXs2aContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = UpdateAuthMapperFromAisRequestAccountListTest.TestConfig.class)
public class UpdateAuthMapperFromAisRequestAccountListTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/account_list_context_from_ctx_";

    @Autowired
    private UpdateAuthMapper.FromAisRequestAccountList mapper;

    @Test
    @SneakyThrows
    public void fromAisRequestAccountListMapperTest() {
        // Given
        AuthorizationRequest mappingInput = getFromFile(PATH_PREFIX + "authorization_request_input.json", AuthorizationRequest.class);
        AccountListXs2aContext expected = getFromFile(PATH_PREFIX + "authorization_request_output.json", AccountListXs2aContext.class);
        AccountListXs2aContext actual = new AccountListXs2aContext();

        // When
        mapper.map(mappingInput, actual);

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