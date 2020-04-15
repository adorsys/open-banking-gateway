package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.fintech.tests.e2e.config.ConsentAuthApproachState;
import de.adorsys.fintech.tests.e2e.config.SmokeConfig;
import de.adorsys.fintech.tests.e2e.steps.FintechServer;
import de.adorsys.fintech.tests.e2e.steps.UserInformationResult;
import de.adorsys.fintech.tests.e2e.steps.WebDriverBasedUserInfoFintech;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@JGivenStage
@ActiveProfiles("test-mocked-fintech")
@ExtendWith(SeleniumExtension.class)
@SpringBootTest(classes = JGivenConfig.class, webEnvironment = NONE)
public class FintechConsentUiSmokeE2ETest extends SpringScenarioTest<FintechServer, WebDriverBasedUserInfoFintech<? extends WebDriverBasedUserInfoFintech<?>>, UserInformationResult> {

    @Autowired
    private SmokeConfig smokeConfig;

    @Autowired
    private ConsentAuthApproachState state;

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
    void testAntonBruecknerWantsToSeeItsAccountsAndTransanctionsFromFintech(FirefoxDriver firefoxDriver) {
        given().fintech_points_to_fintechui_login_page(smokeConfig.getFintechServerUri());
        when().user_already_login_in_bank_profile(firefoxDriver)
                .and()
                .user_accepts_to_get_redirected_to_consentui(firefoxDriver)
                .and()
                .user_anton_brueckner_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_transactions_consent(firefoxDriver)
                .and()
                .user_anton_brueckner_in_consent_ui_reviews_transaction_consent_and_accepts(firefoxDriver)
                .and()
                .user_anton_brueckner_in_consent_ui_sees_redirection_info_to_aspsp_and_accepts(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_from_consent_ui_navigates_to_bank_auth_page(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_inputs_username_and_password(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_confirms_consent_information(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_selects_sca_method(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_provides_sca_challenge_result(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_anton_brueckner_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(firefoxDriver)
                .and().user_navigates_to_page(firefoxDriver)
                .and().user_sees_account_and_list_transactions(firefoxDriver);

        then()
                .fintech_can_read_anton_brueckner_accounts_and_transactions();
    }
}