package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.fintech.tests.e2e.config.ConsentAuthApproachState;
import de.adorsys.fintech.tests.e2e.config.SmokeConfig;
import de.adorsys.fintech.tests.e2e.steps.FintechServer;
import de.adorsys.fintech.tests.e2e.steps.UserInformationResult;
import de.adorsys.fintech.tests.e2e.steps.WebDriverBasedUserInfoFintech;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.ADORSYS_XS2A;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.EMBEDDED_MODE;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.PIN;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.REDIRECT_MODE;
import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_SMOKE_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SuppressWarnings({"PMD.UnusedPrivateField"})
@ActiveProfiles("test-smoke-fintech")
@EnabledIfEnvironmentVariable(named = ENABLE_SMOKE_TESTS, matches = TRUE_BOOL)
@ExtendWith({SeleniumJupiter.class})
@SpringBootTest(classes = {JGivenConfig.class, SmokeConfig.class}, webEnvironment = NONE)
public class FintechConsentUiSmokeE2ETest extends SpringScenarioTest<FintechServer, WebDriverBasedUserInfoFintech<? extends WebDriverBasedAccountInformation<?>>, UserInformationResult> {

    public final String username = "tom" + RandomString.make().toLowerCase();
    public final String fintech_login = "fintech" + RandomString.make().toLowerCase();

    @Autowired
    private SmokeConfig smokeConfig;

    @Autowired
    private ConsentAuthApproachState state;

    @MockBean
    private RequestSigningService requestSigningService;

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

    @SneakyThrows
    @Test
    void testRedirectUserWantsToSeeItsAccountsFromFintech(FirefoxDriver firefoxDriver) {
        given().create_new_user_in_sandbox_tpp_management(username, PIN)
                .enabled_redirect_sandbox_mode(smokeConfig.getAspspProfileServerUri())
                .fintech_points_to_fintech_server(smokeConfig.getFintechServerUri());

        when().user_already_login_in_bank_profile(firefoxDriver, username, fintech_login, REDIRECT_MODE, username)
                .and()
                .user_user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_transactions_consent(firefoxDriver, username)
                .and()
                .user_user_in_consent_ui_reviews_transaction_consent_and_accepts(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_redirection_info_to_aspsp_and_accepts(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_from_consent_ui_navigates_to_bank_auth_page(firefoxDriver)
                .and()
                .sandbox_user_inputs_username_and_password(firefoxDriver, username)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_confirm_login(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .sandbox_user_selects_sca_method(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .sandbox_user_provides_sca_challenge_result(firefoxDriver)
                .and()
                .user_clicks_redirect_back_to_tpp_button(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_anton_brueckner_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver);

        then().fintech_can_read_anton_brueckner_accounts_and_transactions();
    }

    @SneakyThrows
    @Test
    public void testEmbeddedUserWantsItsAccountsFromFintech(FirefoxDriver firefoxDriver) {
        given().create_new_user_in_sandbox_tpp_management(username, PIN)
                .enabled_embedded_sandbox_mode(smokeConfig.getAspspProfileServerUri())
                .fintech_points_to_fintech_server(smokeConfig.getFintechServerUri());

        when().user_consent_authorization_in_embedded_mode(firefoxDriver, username, fintech_login, EMBEDDED_MODE, username);

        then().fintech_can_read_user_accounts();
    }

    @SneakyThrows
    @Test
    void testEmbeddedUserWantsToSeeItsAccountsFromFintech(FirefoxDriver firefoxDriver) {
        given().create_new_user_in_sandbox_tpp_management(username, PIN)
                .enabled_embedded_sandbox_mode(smokeConfig.getAspspProfileServerUri())
                .fintech_points_to_fintech_server(smokeConfig.getFintechServerUri());
        when().user_already_login_in_bank_profile(firefoxDriver, username, fintech_login, EMBEDDED_MODE, username)
                .and()
                .user_for_embeeded_provided_to_consent_ui_initial_parameters_to_list_transactions_with_all_accounts_consent(firefoxDriver, username)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_pin_for_embeeded(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_sca_select_and_selected_type_email2_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_sca_result_to_embedded_authorization_for_redirect(firefoxDriver)
                .and()
                .user_anton_brueckner_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_sees_account_and_list_transactions(firefoxDriver);

        then()
                .fintech_can_read_anton_brueckner_accounts_and_transactions();
    }

    @SneakyThrows
    @Test
    public void testUserAfterLoginWantsToLogout(FirefoxDriver firefoxDriver) {
        given().create_new_user_in_sandbox_tpp_management(username, PIN)
                .enabled_embedded_sandbox_mode(smokeConfig.getAspspProfileServerUri())
                .fintech_points_to_fintech_server(smokeConfig.getFintechServerUri());

        when().user_opens_fintechui_login_page(firefoxDriver)
                .and()
                .user_login_with_its_credentials(firefoxDriver, username)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_looks_for_a_bank_in_the_bank_search_input_place(firefoxDriver, ADORSYS_XS2A)
                .and()
                .user_wait_for_the_result_in_bank_search_and_select(firefoxDriver, REDIRECT_MODE)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_back_to_bank_search(firefoxDriver)
                .and()
                .user_after_login_wants_to_logout(firefoxDriver)
                .and()
                .user_click_on_logout_button(firefoxDriver);

        then().fintech_navigates_back_to_login_after_user_logs_out();
    }

    @SneakyThrows
    @Test
    public void testRedirectUserToSeeItsAccountsFromFintech(FirefoxDriver firefoxDriver) {
        given().create_new_user_in_sandbox_tpp_management(username, PIN)
                .enabled_embedded_sandbox_mode(smokeConfig.getAspspProfileServerUri())
                .fintech_points_to_fintech_server(smokeConfig.getFintechServerUri());

        when().user_authorizes_payment_in_redirect_mode(firefoxDriver, username, fintech_login, ADORSYS_XS2A, username);

        then().fintech_can_read_user_accounts();
    }

}
