package de.adorsys.opba.smoketests;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.smoketests.config.FintechRequestSigningTestConfig;
import de.adorsys.opba.smoketests.config.SmokeConfig;
import de.adorsys.opba.smoketests.config.WebDriverErrorReportAspectAndWatcher;
import de.adorsys.opba.smoketests.steps.SmokeSandboxServers;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
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
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.HBCI_SANDBOX_BANK_SCA_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.MAX_MUSTERMAN;
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
class OpbaApiWithHbciConsentUiSmokeE2ETest extends SpringScenarioTest<SmokeSandboxServers, WebDriverBasedAccountInformation<? extends WebDriverBasedAccountInformation<?>>, AccountInformationResult> {

    private final String opbaLogin = UUID.randomUUID().toString();
    private final String opbaPassword = UUID.randomUUID().toString();

    @Autowired
    private SmokeConfig config;

    @BeforeAll
    static void setupDriverArch() {
        WebDriverManager.firefoxdriver().arch64();
    }

    @Test
    void testAccountsListHbci(FirefoxDriver firefoxDriver) {
        given()
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(config.getOpbaServerUri());

        when()
                .fintech_calls_list_accounts_for_anton_brueckner(HBCI_SANDBOX_BANK_SCA_ID)
                .and()
                .user_opens_opba_consent_login_page(firefoxDriver)
                .and()
                .user_sees_register_button_clicks_it_navigate_to_register_fills_form_and_registers(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_logs_in_to_opba(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_hbci_consent(firefoxDriver, ANTON_BRUECKNER)
                .and()
                .user_in_consent_ui_reviews_account_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_pin(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver);

        then()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_anton_brueckner_hbci_account_data_using_consent_bound_to_service_session(HBCI_SANDBOX_BANK_SCA_ID);
    }

    @Test
    void testTransactionListHbci(FirefoxDriver firefoxDriver) {
        given()
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(config.getOpbaServerUri());

        when()
                .fintech_calls_list_transactions_for_max_musterman("DE59300000033466865655", HBCI_SANDBOX_BANK_SCA_ID)
                .and()
                .user_opens_opba_consent_login_page(firefoxDriver)
                .and()
                .user_sees_register_button_clicks_it_navigate_to_register_fills_form_and_registers(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_logs_in_to_opba(firefoxDriver, opbaLogin, opbaPassword)
                .and()
                .user_provided_to_consent_ui_initial_parameters_to_list_transactions_with_hbci_consent(firefoxDriver, MAX_MUSTERMAN)
                .and()
                .user_in_consent_ui_reviews_transaction_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_pin(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_sca_select_and_selected_type(firefoxDriver, "pushTAN")
                .and()
                .user_in_consent_ui_provides_sca_result_to_embedded_authorization(firefoxDriver, "pushTAN")
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver);

        then()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session(
                        "DE59300000033466865655", HBCI_SANDBOX_BANK_SCA_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }
}
