package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import de.adorsys.xs2a.adapter.service.model.AccountAccess;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AccountAccessType.ALL_ACCOUNTS;
import static de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AccountAccessType.ALL_ACCOUNTS_WITH_BALANCES;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class AisConsentInitiateBodyToXs2aApiTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/consent_body_to_xs2a_api_";

    @Autowired
    private AisConsentInitiateBody.ToXs2aApi mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void aisConsentInitiateBodyMapperTest() {
        // Given
        Xs2aAisContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "ais_consent_input.json", AccountListXs2aContext.class);
        Consents expected = fixtureProvider.getFromFile(PATH_PREFIX + "ais_consent_output.json", Consents.class);

        // When
        Consents actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }

    @Test
    @SneakyThrows
    public void accountsTest_success() {
        // Given
        String mappingInput = ALL_ACCOUNTS.getApiName();
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
        String mappingInput = ALL_ACCOUNTS_WITH_BALANCES.getApiName();
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
        String mappingInput = ALL_ACCOUNTS.getApiName();
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
}