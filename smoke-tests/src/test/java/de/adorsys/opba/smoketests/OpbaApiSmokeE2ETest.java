package de.adorsys.opba.smoketests;

import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.smoketests.config.SandboxConsentAuthApproachState;
import de.adorsys.opba.smoketests.config.SmokeConfig;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_SMOKE_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.SMOKE_TEST;
import static de.adorsys.opba.smoketests.config.SmokeConfig.BOTH_BOOKING;
import static de.adorsys.opba.smoketests.config.SmokeConfig.DATE_FROM;
import static de.adorsys.opba.smoketests.config.SmokeConfig.DATE_TO;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

/**
 * Happy-path smoke test to validate that OpenBanking environment is in sane state.
 */
@EnabledIfEnvironmentVariable(named = ENABLE_SMOKE_TESTS, matches = TRUE_BOOL)
@ExtendWith(SeleniumExtension.class)
@SpringBootTest(classes = {JGivenConfig.class, SmokeConfig.class}, webEnvironment = NONE)
@ActiveProfiles(profiles = {SMOKE_TEST})
class OpbaApiSmokeE2ETest extends SpringScenarioTest<SandboxServers, WebDriverBasedAccountInformation<? extends WebDriverBasedAccountInformation<?>>, AccountInformationResult> {

    private final String OPBA_LOGIN = UUID.randomUUID().toString();
    private final String OPBA_PASSWORD = UUID.randomUUID().toString();

    @Autowired
    private SmokeConfig config;

    @Autowired
    private SandboxConsentAuthApproachState state;

    @BeforeEach
    void memoizeConsentAuthorizationPreference() {
        state.memoize();
    }

    @AfterEach
    void restoreConsentAuthorizationPreference() {
        state.restore();
    }

    @BeforeAll
    static void setupDriverArch() {
        WebDriverManager.firefoxdriver().arch64();
    }

    @Test
    public void testAccountsListWithConsentUsingRedirect(FirefoxDriver firefoxDriver) {
        redirectListAntonBruecknerAccounts(firefoxDriver);
    }

    @Test
    public void testTransactionListWithConsentUsingRedirect(FirefoxDriver firefoxDriver) {
        String accountResourceId = JsonPath
            .parse(redirectListAntonBruecknerAccounts(firefoxDriver)).read("$.accounts[0].resourceId");

        given()
            .enabled_redirect_sandbox_mode(config.getAspspProfileServerUri())
            .rest_assured_points_to_opba_server(config.getOpbaServerUri());

        when()
            .fintech_calls_list_transactions_for_anton_brueckner(accountResourceId)
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_anton_brueckner_provided_initial_parameters_to_list_transactions_with_single_account_consent()
            .and()
            .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
            .and()
            .sandbox_anton_brueckner_navigates_to_bank_auth_page(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_inputs_username_and_password(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_confirms_consent_information(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_selects_sca_method(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_provides_sca_challenge_result(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_only(firefoxDriver);

        then()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_reads_anton_brueckner_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                accountResourceId, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    @Test
    void testAccountsListWithConsentUsingEmbedded() {
        embeddedListMaxMustermanAccounts();
    }

    @Test
    void testTransactionsListWithConsentUsingEmbedded() {
        String accountResourceId = JsonPath
            .parse(embeddedListMaxMustermanAccounts())
            .read("$.accounts[0].resourceId");

        given()
            .enabled_embedded_sandbox_mode(config.getAspspProfileServerUri())
            .rest_assured_points_to_opba_server(config.getOpbaServerUri());

        when()
            .fintech_calls_list_transactions_for_max_musterman(accountResourceId)
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_email1_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok();
        then()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_reads_max_musterman_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                accountResourceId, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    private String embeddedListMaxMustermanAccounts() {
        given()
            .enabled_embedded_sandbox_mode(config.getAspspProfileServerUri())
            .rest_assured_points_to_opba_server(config.getOpbaServerUri())
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
            .fintech_calls_list_accounts_for_max_musterman()
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_accounts_all_accounts_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_email2_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok();

        AccountInformationResult result = then()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(false);

        return result.getResponseContent();
    }

    private String redirectListAntonBruecknerAccounts(FirefoxDriver firefoxDriver) {
        given()
            .enabled_redirect_sandbox_mode(config.getAspspProfileServerUri())
            .rest_assured_points_to_opba_server(config.getOpbaServerUri())
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
            .fintech_calls_list_accounts_for_anton_brueckner()
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
            .and()
            .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
            .and()
            .sandbox_anton_brueckner_navigates_to_bank_auth_page(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_inputs_username_and_password(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_confirms_consent_information(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_selects_sca_method(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_provides_sca_challenge_result(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_only(firefoxDriver);

        AccountInformationResult result = then()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(false);

        return result.getResponseContent();
    }
}
