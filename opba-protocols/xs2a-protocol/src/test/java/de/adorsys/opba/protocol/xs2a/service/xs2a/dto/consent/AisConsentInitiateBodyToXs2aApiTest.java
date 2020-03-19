package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.xs2a.adapter.service.model.AccountAccess;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AisConsentInitiateBodyToXs2aApiTest.TestConfig.class)
public class AisConsentInitiateBodyToXs2aApiTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
                                                           .registerModule(new JavaTimeModule())
                                                           .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/consent_body_to_xs2a_api_";

    @Autowired
    private AisConsentInitiateBody.ToXs2aApi mapper;

    @SneakyThrows
    private <T> T getFromFile(String path, Class<T> valueType) {
        return JSON_MAPPER.readValue(Resources.getResource(path), valueType);
    }

    @Test
    @SneakyThrows
    public void aisConsentInitiateBodyMapperTest() {
        // Given
        Xs2aAisContext mappingInput = getFromFile(PATH_PREFIX + "ais_consent_input.json", AccountListXs2aContext.class);
        Consents expected = getFromFile(PATH_PREFIX + "ais_consent_output.json", Consents.class);

        // When
        Consents actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }

    @Test
    @SneakyThrows
    public void accountsTest_success() {
        // Given
        String mappingInput = "ALL_ACCOUNTS";
        AccountAccess.AvailableAccountsEnum expected = AccountAccess.AvailableAccountsEnum.ALLACCOUNTS;

        // When
        AccountAccess.AvailableAccountsEnum actual = mapper.accounts(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @SneakyThrows
    public void accountsTest_null_result() {
        // Given
        String mappingInput = "WRONG_VALUE";
        AccountAccess.AvailableAccountsEnum expected = null;

        // When
        AccountAccess.AvailableAccountsEnum actual = mapper.accounts(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @SneakyThrows
    public void accountsWithBalanceTest_success() {
        // Given
        String mappingInput = "ALL_ACCOUNTS_WITH_BALANCES";
        AccountAccess.AvailableAccountsWithBalance expected = AccountAccess.AvailableAccountsWithBalance.ALLACCOUNTS;

        // When
        AccountAccess.AvailableAccountsWithBalance actual = mapper.accountsWithBalance(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @SneakyThrows
    public void accountsWithBalanceTest_null_result() {
        // Given
        String mappingInput = "WRONG_VALUE";
        AccountAccess.AvailableAccountsWithBalance expected = null;

        // When
        AccountAccess.AvailableAccountsWithBalance actual = mapper.accountsWithBalance(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @SneakyThrows
    public void allPsd2Test_success() {
        // Given
        String mappingInput = "ALL_ACCOUNTS";
        AccountAccess.AllPsd2Enum expected = AccountAccess.AllPsd2Enum.ALLACCOUNTS;

        // When
        AccountAccess.AllPsd2Enum actual = mapper.allPsd2(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @SneakyThrows
    public void allPsd2Test_null_result() {
        // Given
        String mappingInput = "WRONG_VALUE";
        AccountAccess.AllPsd2Enum expected = null;

        // When
        AccountAccess.AllPsd2Enum actual = mapper.allPsd2(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.protocol.xs2a.service.mappers.generated")
    public static class TestConfig {
    }
}