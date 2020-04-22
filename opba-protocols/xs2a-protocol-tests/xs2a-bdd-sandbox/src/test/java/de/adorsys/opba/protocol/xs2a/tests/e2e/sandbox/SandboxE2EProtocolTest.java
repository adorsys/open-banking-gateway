package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox;

import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.config.RetryableConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.protocol.xs2a.testsandbox.SandboxAppsStarter;
import de.adorsys.psd2.sandbox.cms.starter.Xs2aCmsAutoConfiguration;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.time.LocalDate;
import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_HEAVY_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path heavy test that uses Dynamic-Sandbox to drive banking-protocol.
 */
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
class SandboxE2EProtocolTest extends SpringScenarioTest<SandboxServers, WebDriverBasedAccountInformation<? extends WebDriverBasedAccountInformation<?>>, AccountInformationResult> {

    private static final LocalDate DATE_FROM = LocalDate.parse("2018-01-01");
    private static final LocalDate DATE_TO = LocalDate.parse("2020-09-30");
    private static final String BOTH_BOOKING = "BOTH";

    private static final SandboxAppsStarter executor = new SandboxAppsStarter();

    private final String OPBA_LOGIN = UUID.randomUUID().toString();
    private final String OPBA_PASSWORD = UUID.randomUUID().toString();

    @LocalServerPort
    private int port;

    @Autowired
    private ProtocolConfiguration configuration;

    @BeforeAll
    static void startSandbox() {
        WebDriverManager.firefoxdriver().arch64();

        if (null != System.getenv("NO_SANDBOX_START")) {
            return;
        }

        executor.runAll();
        executor.awaitForAllStarted();
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
    }

    @AfterAll
    static void stopSandbox() {
        executor.shutdown();
    }

    // See https://github.com/spring-projects/spring-boot/issues/14879 for the 'why setting port'
    @BeforeEach
    void setBaseUrl() {
        ProtocolConfiguration.Redirect.Consent consent = configuration.getRedirect().getConsentAccounts();
        consent.setOk(consent.getOk().replaceAll("localhost:\\d+", "localhost:" + port));
        consent.setNok(consent.getNok().replaceAll("localhost:\\d+", "localhost:" + port));
    }

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
            .rest_assured_points_to_opba_server();

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
            .open_banking_has_consent_for_anton_brueckner_transaction_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
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
            .rest_assured_points_to_opba_server();

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

    private String embeddedListMaxMustermanAccounts(Approach expectedApproach) {
        given()
            .enabled_embedded_sandbox_mode()
            .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
            .rest_assured_points_to_opba_server()
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
            .open_banking_has_consent_for_max_musterman_account_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(false);

        return result.getResponseContent();
    }

    private String redirectListAntonBruecknerAccounts(Approach expectedApproach, FirefoxDriver firefoxDriver) {
        given()
            .enabled_redirect_sandbox_mode()
            .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
            .rest_assured_points_to_opba_server()
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
            .open_banking_has_consent_for_anton_brueckner_account_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(false);

        return result.getResponseContent();
    }
}
