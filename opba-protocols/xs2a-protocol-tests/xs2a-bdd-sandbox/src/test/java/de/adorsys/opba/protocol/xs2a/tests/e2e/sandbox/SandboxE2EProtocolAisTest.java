package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox;

import com.jayway.jsonpath.JsonPath;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.config.RetryableConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.psd2.sandbox.cms.starter.Xs2aCmsAutoConfiguration;
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_HEAVY_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SANDBOX_OAUTH2_INTEGRATED_BANK_ID;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path heavy test that uses Dynamic-Sandbox to drive banking-protocol.
 */
@SuppressWarnings("CPD-START") // Same steps are used, but that's fine for readability
@EnabledIfEnvironmentVariable(named = ENABLE_HEAVY_TESTS, matches = TRUE_BOOL)
@EnableAutoConfiguration(exclude = {
    HypermediaAutoConfiguration.class,
    Xs2aCmsAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class,
    SecurityAutoConfiguration.class,
})
@ExtendWith(SeleniumExtension.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {RetryableConfig.class, Xs2aRealSandboxProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
class SandboxE2EProtocolAisTest extends SandboxCommonTest<
        SandboxServers<? extends SandboxServers<?>>,
        WebDriverBasedAccountInformation<? extends WebDriverBasedAccountInformation<?>>,
        AccountInformationResult<? extends AccountInformationResult<?>>> {

    @ParameterizedTest
    @EnumSource(Approach.class)
    public void testAccountsListWithConsentUsingRedirect(Approach expectedApproach, FirefoxDriver firefoxDriver) {
        redirectListAntonBruecknerAccounts(expectedApproach, firefoxDriver);
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    public void testTransactionListWithConsentUsingRedirect(Approach expectedApproach, FirefoxDriver firefoxDriver) {
        String accountResourceId = JsonPath
            .parse(redirectListAntonBruecknerAccounts(expectedApproach, firefoxDriver)).read("$.accounts[0].resourceId");

        given()
            .enabled_redirect_sandbox_mode()
            .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api();

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
            .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(firefoxDriver);

        then()
            .open_banking_has_consent_for_anton_brueckner_transaction_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_reads_anton_brueckner_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                accountResourceId, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    public void testTransactionListWithConsentUsingRedirectUsingEndpointWithoutResourceId(Approach expectedApproach, FirefoxDriver firefoxDriver) {

        given()
                .enabled_redirect_sandbox_mode()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_list_transactions_for_anton_brueckner()
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
                .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(firefoxDriver);

        AccountInformationResult<? extends AccountInformationResult<?>> accountsResult = then()
                .open_banking_has_consent_for_anton_brueckner_transaction_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(false);
        String accountResourceId = JsonPath.parse(accountsResult.getResponseContent()).read("$.accounts[0].resourceId");
        then()
                .open_banking_reads_anton_brueckner_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                        accountResourceId, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testAccountsListWithConsentUsingEmbedded(Approach expectedApproach) {
        embeddedListMaxMustermanAccounts(expectedApproach);
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testTransactionsListWithConsentUsingEmbedded(Approach expectedApproach) {
        String accountResourceId = JsonPath
            .parse(embeddedListMaxMustermanAccounts(expectedApproach))
            .read("$.accounts[0].resourceId");

        given()
            .enabled_embedded_sandbox_mode()
            .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api();

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
            .open_banking_has_consent_for_max_musterman_transaction_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_reads_max_musterman_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                accountResourceId, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testTransactionsListWithConsentUsingEmbeddedUsingEndpointWithoutResourceId(Approach expectedApproach) {

        given()
                .enabled_embedded_sandbox_mode()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_list_transactions_for_max_musterman()
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
        AccountInformationResult<? extends AccountInformationResult<?>> accountsResult = then()
                .open_banking_has_consent_for_max_musterman_transaction_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(false);
        String accountResourceId = JsonPath.parse(accountsResult.getResponseContent()).read("$.accounts[0].resourceId");

        then()
                .open_banking_reads_max_musterman_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                        accountResourceId, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }

    /**
     * Not using {@code ParameterizedTest} as OAuth2 is the special case of REDIRECT (to reduce pipeline runtime).
     */
    @Test
    public void testAccountsListWithConsentUsingOAuth2PreStep(FirefoxDriver firefoxDriver) {
        given()
                .enabled_oauth2_pre_step_sandbox_mode()
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
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
                .add_open_banking_auth_session_key_cookie_to_selenium(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_inputs_username_and_password_for_oauth2_form(firefoxDriver)
                .and()
                .update_redirect_code_from_browser_url(firefoxDriver)
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
                .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(firefoxDriver);

        then()
                .open_banking_has_consent_for_anton_brueckner_account_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(false);
    }

    /**
     * Not using {@code ParameterizedTest} as OAuth2 is the special case of REDIRECT (to reduce pipeline runtime).
     */
    @Test
    public void testAccountsListWithConsentUsingOAuth2Integrated(FirefoxDriver firefoxDriver) {
        given()
                .enabled_redirect_sandbox_mode()
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                /*
                 * FIXME: Using custom bank id because of https://github.com/adorsys/xs2a/issues/73
                 */
                .fintech_calls_list_accounts_for_anton_brueckner(SANDBOX_OAUTH2_INTEGRATED_BANK_ID)
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
                .sandbox_anton_brueckner_imitates_click_redirect_back_to_tpp_button_api_localhost_cookie_only_with_oauth2_integrated_hack(firefoxDriver);

        then()
                .open_banking_has_consent_for_anton_brueckner_account_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(false);
    }

    private String embeddedListMaxMustermanAccounts(Approach expectedApproach) {
        given()
            .enabled_embedded_sandbox_mode()
            .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
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

        AccountInformationResult<? extends AccountInformationResult<?>> result = then()
                .open_banking_has_consent_for_max_musterman_account_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(false);

        return result.getResponseContent();
    }

    private String redirectListAntonBruecknerAccounts(Approach expectedApproach, FirefoxDriver firefoxDriver) {
        given()
            .enabled_redirect_sandbox_mode()
            .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
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
            .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(firefoxDriver);

        AccountInformationResult<? extends AccountInformationResult<?>> result = then()
                .open_banking_has_consent_for_anton_brueckner_account_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(false);

        return result.getResponseContent();
    }
}
