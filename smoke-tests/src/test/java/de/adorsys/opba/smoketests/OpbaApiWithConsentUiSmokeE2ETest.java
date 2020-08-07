package de.adorsys.opba.smoketests;

import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.smoketests.config.FintechRequestSigningTestConfig;
import de.adorsys.opba.smoketests.config.SandboxConsentAuthApproachState;
import de.adorsys.opba.smoketests.config.SmokeConfig;
import de.adorsys.opba.smoketests.config.WebDriverErrorReportAspectAndWatcher;
import de.adorsys.opba.smoketests.steps.SmokeSandboxServers;
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
 * Happy-path smoke test to validate that OpenBanking environment with Consent UI is in sane state.
 */
@EnabledIfEnvironmentVariable(named = ENABLE_SMOKE_TESTS, matches = TRUE_BOOL)
@ExtendWith({SeleniumExtension.class, WebDriverErrorReportAspectAndWatcher.class})
@SpringBootTest(classes = {JGivenConfig.class, SmokeConfig.class, FintechRequestSigningTestConfig.class, SmokeSandboxServers.class}, webEnvironment = NONE)
// Use @ActiveProfiles(profiles = {SMOKE_TEST, "test-smoke-local"}) to run the test on local env.
@ActiveProfiles(profiles = {SMOKE_TEST})
class OpbaApiWithConsentUiSmokeE2ETest extends SpringScenarioTest<SmokeSandboxServers, WebDriverBasedAccountInformation<? extends WebDriverBasedAccountInformation<?>>, AccountInformationResult> {

    private final String opbaLogin = UUID.randomUUID().toString();
    private final String opbaPassword = UUID.randomUUID().toString();
    private final String sandboxUserLogin = UUID.randomUUID().toString();
    private final String sandboxUserPassword = UUID.randomUUID().toString();

    @Autowired
    private SmokeConfig config;

    @Autowired
    private SandboxConsentAuthApproachState state;

    @BeforeAll
    static void setupDriverArch() {
        WebDriverManager.firefoxdriver().arch64();
    }

    @BeforeEach
    void memoizeConsentAuthorizationPreference() {
        state.memoize();
    }

    @AfterEach
    void restoreConsentAuthorizationPreference() {
        state.restore();
    }

    @Test
    public void testAccountsListWithConsentUsingRedirectAllAccountsConsent(FirefoxDriver firefoxDriver) {
        redirectListUserAccounts(firefoxDriver);
    }

    @Test
    public void testTransactionListWithConsentUsingRedirectAllAccountsConsent(FirefoxDriver firefoxDriver) {
        String accountResourceId = JsonPath
                                           .parse(redirectListUserAccounts(firefoxDriver)).read("$.accounts[0].resourceId");

        given()
                .enabled_redirect_sandbox_mode(config.getAspspProfileServerUri())
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(config.getOpbaServerUri());

        when()
                .fintech_calls_list_transactions_for_user(sandboxUserLogin, accountResourceId)
                .and()
                .user_opens_opba_consent_login_page(firefoxDriver)
                .and()
                .user_logs_in_to_opba(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_provided_to_consent_ui_initial_parameters_to_list_transactions_with_all_accounts_consent(firefoxDriver, sandboxUserLogin)
                .and()
                .user_in_consent_ui_reviews_transaction_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_redirection_info_to_aspsp_and_accepts(firefoxDriver)
                .and()
                .sandbox_user_from_consent_ui_navigates_to_bank_auth_page(firefoxDriver)
                .and()
                .sandbox_user_inputs_username_and_password(firefoxDriver, sandboxUserLogin, sandboxUserPassword)
                .and()
                .sandbox_user_confirms_consent_information(firefoxDriver)
                .and()
                .sandbox_user_selects_sca_method(firefoxDriver)
                .and()
                .sandbox_user_provides_sca_challenge_result(firefoxDriver)
                .and()
                .sandbox_user_clicks_redirect_back_to_tpp_button(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver);

        then()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_reads_user_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                        sandboxUserLogin, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }

    @Test
    void testAccountsListWithConsentUsingEmbeddedAllAccountConsent(FirefoxDriver firefoxDriver) {
        embeddedListAccountsAllAccountConsent(firefoxDriver);
    }

    @Test
    void testTransactionsListWithConsentUsingEmbeddedAllAccountConsent(FirefoxDriver firefoxDriver) {
        String accountResourceId = JsonPath
                                           .parse(embeddedListAccountsAllAccountConsent(firefoxDriver))
                                           .read("$.accounts[0].resourceId");

        given()
                .enabled_embedded_sandbox_mode(config.getAspspProfileServerUri())
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(config.getOpbaServerUri());

        when()
                .fintech_calls_list_transactions_for_user(sandboxUserLogin, accountResourceId)
                .and()
                .user_opens_opba_consent_login_page(firefoxDriver)
                .and()
                .user_logs_in_to_opba(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_provided_to_consent_ui_initial_parameters_to_list_transactions_with_all_accounts_consent(firefoxDriver, sandboxUserLogin)
                .and()
                .user_in_consent_ui_reviews_transactions_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_pin(firefoxDriver, sandboxUserPassword)
                .and()
                .user_in_consent_ui_sees_sca_select_and_selected_type_email1_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_sca_result_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver);
        then()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_reads_user_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                        sandboxUserLogin, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }

    @Test
    void testAccountsListWithConsentUsingEmbeddedDedicatedOneAccountConsent(FirefoxDriver firefoxDriver) {
        embeddedListAccountsDedicatedOneAccountConsent(firefoxDriver);
    }

    @Test
    void testTransactionsListWithConsentUsingEmbeddedDedicatedOneAccountConsent(FirefoxDriver firefoxDriver) {
        String accountResourceId = JsonPath
                                           .parse(embeddedListAccountsDedicatedOneAccountConsent(firefoxDriver))
                                           .read("$.accounts[0].resourceId");

        given()
                .enabled_embedded_sandbox_mode(config.getAspspProfileServerUri())
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(config.getOpbaServerUri());

        when()
                .fintech_calls_list_transactions_for_user(sandboxUserLogin, accountResourceId)
                .and()
                .user_opens_opba_consent_login_page(firefoxDriver)
                .and()
                .user_logs_in_to_opba(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_dedicated_transactions_consent(firefoxDriver, sandboxUserLogin)
                .and()
                .user_provided_to_consent_ui_account_iban_for_dedicated_transactions_consent(firefoxDriver)
                .and()
                .user_in_consent_ui_reviews_transactions_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_pin(firefoxDriver, sandboxUserPassword)
                .and()
                .user_in_consent_ui_sees_sca_select_and_selected_type_email1_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_sca_result_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver);
        then()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_reads_user_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                        sandboxUserLogin, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }

    private String embeddedListAccountsAllAccountConsent(FirefoxDriver firefoxDriver) {
        given()
                .create_new_user_in_sandbox_tpp_management(sandboxUserLogin, sandboxUserPassword)
                .enabled_embedded_sandbox_mode(config.getAspspProfileServerUri())
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(config.getOpbaServerUri());

        when()
                .fintech_calls_list_accounts_for_user(sandboxUserLogin)
                .and()
                .user_opens_opba_consent_login_page(firefoxDriver)
                .and()
                .user_sees_register_button_clicks_it_navigate_to_register_fills_form_and_registers(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_logs_in_to_opba(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_consent(firefoxDriver, sandboxUserLogin)
                .and()
                .user_in_consent_ui_reviews_account_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_pin(firefoxDriver, sandboxUserPassword)
                .and()
                .user_in_consent_ui_sees_sca_select_and_selected_type_email1_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_sca_result_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver);

        AccountInformationResult result = then()
                                                  .fintech_calls_consent_activation_for_current_authorization_id()
                                                  .open_banking_can_read_user_account_data_using_consent_bound_to_service_session(sandboxUserLogin, false);

        return result.getResponseContent();
    }

    private String embeddedListAccountsDedicatedOneAccountConsent(FirefoxDriver firefoxDriver) {
        given()
                .create_new_user_in_sandbox_tpp_management(sandboxUserLogin, sandboxUserPassword)
                .enabled_embedded_sandbox_mode(config.getAspspProfileServerUri())
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(config.getOpbaServerUri());

        when()
                .fintech_calls_list_accounts_for_user(sandboxUserLogin)
                .and()
                .user_opens_opba_consent_login_page(firefoxDriver)
                .and()
                .user_sees_register_button_clicks_it_navigate_to_register_fills_form_and_registers(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_logs_in_to_opba(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_dedicated_accounts_consent(firefoxDriver, sandboxUserLogin)
                .and()
                .user_provided_to_consent_ui_account_iban_for_dedicated_accounts_consent(firefoxDriver)
                .and()
                .user_in_consent_ui_reviews_account_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_pin(firefoxDriver, sandboxUserPassword)
                .and()
                .user_in_consent_ui_sees_sca_select_and_selected_type_email1_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_sca_result_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver);

        AccountInformationResult result = then()
                                                  .fintech_calls_consent_activation_for_current_authorization_id()
                                                  .open_banking_can_read_user_account_data_using_consent_bound_to_service_session(sandboxUserLogin, false);

        return result.getResponseContent();
    }

    private String redirectListUserAccounts(FirefoxDriver firefoxDriver) {
        given()
                .create_new_user_in_sandbox_tpp_management(sandboxUserLogin, sandboxUserPassword)
                .enabled_redirect_sandbox_mode(config.getAspspProfileServerUri())
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(config.getOpbaServerUri());

        when()
                .fintech_calls_list_accounts_for_user(sandboxUserLogin)
                .and()
                .user_opens_opba_consent_login_page(firefoxDriver)
                .and()
                .user_sees_register_button_clicks_it_navigate_to_register_fills_form_and_registers(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_logs_in_to_opba(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_consent(firefoxDriver, sandboxUserLogin)
                .and()
                .user_in_consent_ui_reviews_account_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_redirection_info_to_aspsp_and_accepts(firefoxDriver)
                .and()
                .sandbox_user_from_consent_ui_navigates_to_bank_auth_page(firefoxDriver)
                .and()
                .sandbox_user_inputs_username_and_password(firefoxDriver, sandboxUserLogin, sandboxUserPassword)
                .and()
                .sandbox_user_confirms_consent_information(firefoxDriver)
                .and()
                .sandbox_user_selects_sca_method(firefoxDriver)
                .and()
                .sandbox_user_provides_sca_challenge_result(firefoxDriver)
                .and()
                .sandbox_user_clicks_redirect_back_to_tpp_button(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver);

        AccountInformationResult result = then()
                                                  .fintech_calls_consent_activation_for_current_authorization_id()
                                                  .open_banking_can_read_user_account_data_using_consent_bound_to_service_session(sandboxUserLogin, false);

        return result.getResponseContent();
    }
}
