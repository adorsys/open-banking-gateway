package de.adorsys.opba.protocol.xs2a.service.xs2a.validation;

import de.adorsys.opba.protocol.xs2a.config.ObjectMapperConfig;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AccountAccessBodyValidatorTest.TestConfig.class)
class AccountAccessBodyValidatorTest {
    public static final String PATH_PREFIX = "validator-test-fixtures/";

    @Autowired
    private FixtureProvider fixtureProvider;

    private AccountAccessBodyValidator validator = new AccountAccessBodyValidator();

    @Test
    void dedicate_consent_with_accounts_test() {
        // Given
        AisConsentInitiateBody.AccountAccessBody mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "dedicated_with_accounts_consent.json", AisConsentInitiateBody.AccountAccessBody.class);

        // When
        boolean actual = validator.isValid(mappingInput, null);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void dedicate_consent_without_accounts_with_balances_test() {
        // Given
        AisConsentInitiateBody.AccountAccessBody mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "dedicated_without_accounts_consent_with_balances.json", AisConsentInitiateBody.AccountAccessBody.class);

        // When
        boolean actual = validator.isValid(mappingInput, null);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void dedicate_consent_without_accounts_without_balances_test() {
        // Given
        AisConsentInitiateBody.AccountAccessBody mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "dedicated_without_accounts_consent_without_balances.json", AisConsentInitiateBody.AccountAccessBody.class);

        // When
        boolean actual = validator.isValid(mappingInput, null);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void global_consent_test() {
        // Given
        AisConsentInitiateBody.AccountAccessBody mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "global_consent.json", AisConsentInitiateBody.AccountAccessBody.class);

        // When
        boolean actual = validator.isValid(mappingInput, null);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void bank_offered_consent_test() {
        // Given
        AisConsentInitiateBody.AccountAccessBody mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "bank_offered_consent.json", AisConsentInitiateBody.AccountAccessBody.class);

        // When
        boolean actual = validator.isValid(mappingInput, null);

        // Then
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void invalid_available_accounts_consent_test() {
        // Given
        AisConsentInitiateBody.AccountAccessBody mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "invalid_available_accounts_consent.json", AisConsentInitiateBody.AccountAccessBody.class);

        // When
        boolean actual = validator.isValid(mappingInput, null);

        // Then
        assertThat(actual).isFalse();
    }

    @Configuration
    @Import(ObjectMapperConfig.class)
    @ComponentScan(basePackages = "de.adorsys.opba.protocol.xs2a.util")
    public static class TestConfig {
    }
}