package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentRequestCommon;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WebDriverBasedPaymentInitiation<SELF extends WebDriverBasedPaymentInitiation<SELF>> extends PaymentRequestCommon<SELF> {

    @Autowired
    WebDriverBasedAccountInformation acc;

    public SELF sandbox_anton_brueckner_navigates_to_bank_auth_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        return self();
    }

    public SELF sandbox_anton_brueckner_inputs_username_and_password(WebDriver driver) {
        acc.sandbox_anton_brueckner_inputs_username_and_password(driver);
        return self();
    }

    public SELF sandbox_anton_brueckner_confirms_consent_information(WebDriver driver) {
        acc.sandbox_anton_brueckner_confirms_consent_information(driver);
        return self();
    }

    public SELF sandbox_anton_brueckner_selects_sca_method(WebDriver driver) {
        acc.sandbox_anton_brueckner_selects_sca_method(driver);
        return self();
    }

    public SELF sandbox_anton_brueckner_provides_sca_challenge_result(WebDriver driver) {
        acc.sandbox_anton_brueckner_provides_sca_challenge_result(driver);
        return self();
    }

    // Sending cookie with last request as it doesn't exist in browser for API tests
    // null for cookieDomain is the valid value for localhost tests. This works correctly for localhost.
    public SELF sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(WebDriver driver) {
        acc.sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(driver, authSessionCookie);
        return self();
    }

    /*
     * Caused by FIXME https://github.com/adorsys/XS2A-Sandbox/issues/42, should be sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only
     */
    public SELF sandbox_anton_brueckner_imitates_click_redirect_back_to_tpp_button_api_localhost_cookie_only_with_oauth2_integrated_hack(WebDriver driver) {
        acc.sandbox_anton_brueckner_imitates_click_redirect_back_to_tpp_button_api_localhost_cookie_only_with_oauth2_integrated_hack(driver, authSessionCookie);
        return self();
    }

    public SELF add_open_banking_auth_session_key_cookie_to_selenium(WebDriver driver) {
        acc.add_open_banking_auth_session_key_cookie_to_selenium(driver, authSessionCookie);
        return self();
    }

    public SELF sandbox_anton_brueckner_inputs_username_and_password_for_oauth2_form(WebDriver driver) {
        acc.sandbox_anton_brueckner_inputs_username_and_password_for_oauth2_form(driver);
        return self();
    }

    public SELF update_redirect_code_from_browser_url(WebDriver driver) {
        acc.update_redirect_code_from_browser_on_redirect_back_url(driver);
        this.redirectCode = acc.getRedirectCode();
        return self();
    }
}
