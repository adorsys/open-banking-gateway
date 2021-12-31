package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.fintech.tests.e2e.config.ConsentAuthApproachState;
import de.adorsys.fintech.tests.e2e.config.SmokeConfig;
import de.adorsys.fintech.tests.e2e.steps.FintechServer;
import de.adorsys.fintech.tests.e2e.steps.UserInformationResult;
import de.adorsys.fintech.tests.e2e.steps.WebDriverBasedUserInfoFintech;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.internal.config.CookieProperties;
import de.adorsys.opba.api.security.internal.config.TppTokenProperties;
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
import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_SMOKE_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SuppressWarnings("PMD.UnusedPrivateField")
@ActiveProfiles("test-smoke-fintech")
@EnabledIfEnvironmentVariable(named = ENABLE_SMOKE_TESTS, matches = TRUE_BOOL)
@ExtendWith({SeleniumJupiter.class})
@SpringBootTest(classes = {JGivenConfig.class, SmokeConfig.class}, webEnvironment = NONE)
public class FintechPaymentSmokeE2ETest extends SpringScenarioTest<FintechServer, WebDriverBasedUserInfoFintech<? extends WebDriverBasedUserInfoFintech<?>>, UserInformationResult> {

    public final String username = "tom" + RandomString.make().toLowerCase();
    public final String fintech_login = "fintech" + RandomString.make().toLowerCase();

    @Autowired
    private SmokeConfig smokeConfig;

    @Autowired
    private ConsentAuthApproachState state;

    @MockBean
    private RequestSigningService requestSigningService;

    @MockBean
    private TppTokenProperties tppTokenProperties;

    @MockBean
    private CookieProperties cookieProperties;

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
    void testUserAfterInitiatingAPaymentWantsToSeeItsTransactionsOnFintechRedirectMode(FirefoxDriver firefoxDriver) {
        given().create_new_user_in_sandbox_tpp_management(username, PIN)
                .enabled_redirect_sandbox_mode(smokeConfig.getAspspProfileServerUri())
                .fintech_points_to_fintech_server(smokeConfig.getFintechServerUri());

        when().user_authorizes_payment_in_redirect_mode(firefoxDriver, username, fintech_login, ADORSYS_XS2A, username)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_select_transfert_button(firefoxDriver)
                .and()
                .user_select_account_to_proceed_payment_with(firefoxDriver)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_fills_transfer_formular(firefoxDriver)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_for_embeeded_provided_to_consent_ui_initial_parameters_to_list_transactions_consent(firefoxDriver, username)
                .and()
                .user_confirm_button_for_payment(firefoxDriver)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_inputs_username_and_password_for_redirect(firefoxDriver, username)
                .and()
                .user_confirm_login(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_sca_select_and_confirm_type_email2_to_redirect_authorization(firefoxDriver)
                .and()
                .user_provides_sca_challenge_result_for_redirect(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp_for_redirect(firefoxDriver)
                .and()
                .user_anton_brueckner_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver)
                .and()
                .user_sees_account_and_list_transactions(firefoxDriver);

        then().fintech_can_read_users_accounts_and_transactions();
    }

    @SneakyThrows
    @Test
    void testUserAfterInitiatingAPaymentWantsToSeeItsTransactionsOnFintechEmbeddedMode(FirefoxDriver firefoxDriver) {
        given().create_new_user_in_sandbox_tpp_management(username, PIN)
                .enabled_redirect_sandbox_mode(smokeConfig.getAspspProfileServerUri())
                .fintech_points_to_fintech_server(smokeConfig.getFintechServerUri());

        when().user_consent_authorization_in_embedded_mode(firefoxDriver, username, fintech_login, EMBEDDED_MODE, username)
                .and()
                .user_select_transfert_button(firefoxDriver)
                .and()
                .user_select_account_to_proceed_payment_with(firefoxDriver)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_fills_transfer_formular(firefoxDriver)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_for_embeeded_provided_to_consent_ui_initial_parameters_to_list_transactions_consent(firefoxDriver, username)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_pin_for_embeeded(firefoxDriver)
                .and()
                .user_in_consent_ui_sees_sca_select_and_selected_type_email2_to_embedded_authorization(firefoxDriver)
                .and()
                .user_in_consent_ui_provides_sca_result_to_embedded_authorization_for_redirect(firefoxDriver)
                .and()
                .user_click_on_confirm_button(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver);

        then().fintech_can_read_users_accounts_and_transactions();
    }

}
