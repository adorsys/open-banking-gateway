package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryOperations;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.config.RetryableConfig.TEST_RETRY_OPS;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WebDriverBasedAccountInformation<SELF extends WebDriverBasedAccountInformation<SELF>> extends AccountInformationRequestCommon<SELF> {

    public static final String SUBMIT_ID = "do_submit";
    public static final String ANTON_BRUECKNER = "anton.brueckner";
    public static final String MAX_MUSTERMAN = "max.musterman";
    public static final String PIN_VALUE = "12345";
    public static final String TAN_VALUE = "123456";

    @Autowired
    @Qualifier(TEST_RETRY_OPS)
    private RetryOperations withRetry;

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    public SELF user_opens_opba_consent_login_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        waitForPageLoadAndUrlContains(driver, "/login");
        return self();
    }

    public SELF user_sees_register_button_clicks_it_navigate_to_register_fills_form_and_registers(WebDriver driver, String username, String password) {
        clickOnButton(driver, By.id("register"));
        waitForPageLoadAndUrlEndsWithPath(driver, "/register");
        sendText(driver, By.id("username"), username);
        sendText(driver, By.id("password"), password);
        sendText(driver, By.id("confirmPassword"), password);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_logs_in_to_opba(WebDriver driver, String username, String password) {
        waitForPageLoadAndUrlContains(driver, "/login");
        sendText(driver, By.id("username"), username);
        sendText(driver, By.id("password"), password);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_consent(WebDriver driver, String user) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts");
        sendText(driver, By.id("PSU_ID"), user);
        clickOnButton(driver, By.id("ALL_ACCOUNTS"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_anton_brueckner_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_transactions_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions");
        sendText(driver, By.id("PSU_ID"), ANTON_BRUECKNER);
        clickOnButton(driver, By.id("ALL_PSD2"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_dedicated_transactions_consent(WebDriver driver, String user) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions");
        sendText(driver, By.id("PSU_ID"), user);
        clickOnButton(driver, By.id("FINE_GRAINED"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_provided_to_consent_ui_initial_parameters_to_list_accounts_with_dedicated_accounts_consent(WebDriver driver, String user) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts");
        sendText(driver, By.id("PSU_ID"), user);
        clickOnButton(driver, By.id("FINE_GRAINED"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_provided_to_consent_ui_account_iban_for_dedicated_accounts_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts/dedicated-account-access");
        sendText(driver, By.cssSelector("[id^=account-reference]"), iban);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_provided_to_consent_ui_account_iban_for_dedicated_transactions_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions/dedicated-account-access");
        sendText(driver, By.cssSelector("[id^=account-reference]"), iban);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_provided_to_consent_ui_initial_parameters_to_list_transactions_with_all_accounts_consent(WebDriver driver, String user) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions");
        sendText(driver, By.id("PSU_ID"), user);
        clickOnButton(driver, By.id("ALL_PSD2"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_in_consent_ui_sees_redirection_info_to_aspsp_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "/to-aspsp-redirection");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF sandbox_anton_brueckner_from_consent_ui_navigates_to_bank_auth_page(WebDriver driver) {
        waitForPageLoadAndUrlContains(driver, "account-information/login");
        return self();
    }

    public SELF sandbox_user_from_consent_ui_navigates_to_bank_auth_page(WebDriver driver) {
        waitForPageLoadAndUrlContains(driver, "account-information/login");
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(WebDriver driver) {
        waitForPageLoadAndUrlContains(driver, "/consent-result");
        clickOnButton(driver, By.id(SUBMIT_ID), true);
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(WebDriver driver) {
        waitForPageLoadAndUrlContains(driver, "/consent-result");
        clickOnButton(driver, By.id(SUBMIT_ID), true);
        return self();
    }

    public SELF user_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(WebDriver driver) {
        waitForPageLoadAndUrlContains(driver, "/consent-result");
        clickOnButton(driver, By.id(SUBMIT_ID), true);
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_reviews_transaction_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions/review-consent");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_in_consent_ui_reviews_transaction_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions/review-consent");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_in_consent_ui_reviews_account_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts/review-consent");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_in_consent_ui_reviews_transactions_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions/review-consent");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_in_consent_ui_provides_pin(WebDriver driver, String pin) {
        waitForPageLoadAndUrlEndsWithPath(driver, "authenticate");
        sendText(driver, By.id("pin"), pin);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_in_consent_ui_provides_sca_result_to_embedded_authorization(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "sca-result/EMAIL");
        sendText(driver, By.id("tan"), TAN_VALUE);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_provided_to_consent_ui_initial_parameters_to_list_transactions_with_all_accounts_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions");
        sendText(driver, By.id("PSU_ID"), MAX_MUSTERMAN);
        clickOnButton(driver, By.id("ALL_PSD2"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_reviews_transactions_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions/review-consent");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_provides_pin(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "authenticate");
        sendText(driver, By.id("pin"), PIN_VALUE);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_sees_sca_select_and_selected_type_email2_to_embedded_authorization(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "select-sca-method");
        selectByVisibleInDropdown(driver, By.id("scaMethod"), "EMAIL:max.musterman2@mail.de");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_provides_sca_result_to_embedded_authorization(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "sca-result");
        sendText(driver, By.id("tan"), TAN_VALUE);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_in_consent_ui_sees_sca_select_and_selected_type_email1_to_embedded_authorization(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "select-sca-method");
        selectByVisibleInDropdown(driver, By.id("scaMethod"), "EMAIL:test_static@example.com");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF sandbox_anton_brueckner_navigates_to_bank_auth_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        return self();
    }

    public SELF sandbox_user_navigates_to_bank_auth_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        return self();
    }

    public SELF sandbox_anton_brueckner_inputs_username_and_password(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.name("login"));
        doFillLoginFormByAntonBruecknerInSandbox(driver, ANTON_BRUECKNER, PIN_VALUE);
        return self();
    }

    public SELF update_redirect_code_from_browser_url(WebDriver driver) {
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(driver.getCurrentUrl()).build().getQueryParams();
        this.redirectCode = parameters.getFirst(REDIRECT_CODE_QUERY);
        return self();
    }

    public SELF sandbox_anton_brueckner_inputs_username_and_password_for_oauth2_form(WebDriver driver) {
        waitForPageLoad(driver);
        doFillLoginFormByAntonBruecknerInSandbox(driver, ANTON_BRUECKNER, PIN_VALUE, true);
        return self();
    }

    public SELF sandbox_user_inputs_username_and_password(WebDriver driver, String user, String password) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.name("login"));
        doFillLoginFormByAntonBruecknerInSandbox(driver, user, password);
        return self();
    }

    public SELF sandbox_anton_brueckner_confirms_consent_information(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_user_confirms_consent_information(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_anton_brueckner_selects_sca_method(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_user_selects_sca_method(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_anton_brueckner_provides_sca_challenge_result(WebDriver driver) {
        waitForPageLoad(driver);
        sendText(driver, By.name("authCode"), "123456");
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_user_provides_sca_challenge_result(WebDriver driver) {
        waitForPageLoad(driver);
        sendText(driver, By.name("authCode"), "123456");
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF manually_set_authorization_cookie_on_domain(WebDriver driver, String uiUri) {
        waitForPageLoad(driver);
        String restoreUri = driver.getCurrentUrl();
        driver.get(uiUri);
        driver.manage().addCookie(new Cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie, URI.create(uiUri).getHost(), null, null));
        driver.get(restoreUri);
        return self();
    }

    // Sending cookie with last request as it doesn't exist in browser for API tests
    // null for cookieDomain is the valid value for localhost tests. This works correctly for localhost.
    public SELF sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(WebDriver driver, String authSessionCookie) {
        waitForPageLoad(driver);
        add_open_banking_auth_session_key_cookie_to_selenium(driver, authSessionCookie);
        try {
            clickOnButton(driver, By.className("btn-primary"), true);
        } finally {
            driver.manage().deleteCookieNamed(AUTHORIZATION_SESSION_KEY);
        }
        return self();
    }

    /*
   Caused by FIXME https://github.com/adorsys/XS2A-Sandbox/issues/42, should be sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only
    */
    public SELF sandbox_anton_brueckner_imitates_click_redirect_back_to_tpp_button_api_localhost_cookie_only_with_oauth2_integrated_hack(WebDriver driver) {
        return sandbox_anton_brueckner_imitates_click_redirect_back_to_tpp_button_api_localhost_cookie_only_with_oauth2_integrated_hack(driver, authSessionCookie);
    }

    /*
    Caused by FIXME https://github.com/adorsys/XS2A-Sandbox/issues/42, should be sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only
     */
    public SELF sandbox_anton_brueckner_imitates_click_redirect_back_to_tpp_button_api_localhost_cookie_only_with_oauth2_integrated_hack(WebDriver driver, String authSessionCookie) {
        waitForPageLoad(driver);
        add_open_banking_auth_session_key_cookie_to_selenium(driver, authSessionCookie);
        try {
            String redirect = driver.findElement(By.className("btn-primary"))
                    .getAttribute("href")
                    .replaceAll("oauth2=false", "oauth2=true");
            swallowReachedErrorPageException(() -> driver.navigate().to(redirect));
        } finally {
            driver.manage().deleteCookieNamed(AUTHORIZATION_SESSION_KEY);
        }
        return self();
    }

    public SELF add_open_banking_auth_session_key_cookie_to_selenium(WebDriver driver) {
        driver.manage().addCookie(new Cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie));
        return self();
    }

    public SELF add_open_banking_auth_session_key_cookie_to_selenium(WebDriver driver, String authSessionCookie) {
        driver.manage().addCookie(new Cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie));
        return self();
    }

    public SELF sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(WebDriver driver) {
        return sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(driver, authSessionCookie);
    }

    public SELF sandbox_user_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(WebDriver driver) {
        return sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(driver, authSessionCookie);
    }

    public SELF sandbox_user_clicks_redirect_back_to_tpp_button(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.className("btn-primary"), true);
        return self();
    }

    private void doFillLoginFormByAntonBruecknerInSandbox(WebDriver driver, String login, String pin) {
        doFillLoginFormByAntonBruecknerInSandbox(driver, login, pin, false);
    }

    private void doFillLoginFormByAntonBruecknerInSandbox(WebDriver driver, String login, String pin, boolean allowReachedErrorPage) {
        sendText(driver, By.name("login"), login);
        sendText(driver, By.name("pin"), pin);
        clickOnButton(driver, By.xpath("//button[@type='submit']"), allowReachedErrorPage);
    }

    private WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, timeout.getSeconds());
    }

    private void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    private void waitForPageLoadAndUrlContains(WebDriver driver, String urlContains) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd ->
                        driver.getCurrentUrl().contains(urlContains)
                        && ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
                );
    }

    private void waitForPageLoadAndUrlEndsWithPath(WebDriver driver, String urlEndsWithPath) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd ->
                        URI.create(driver.getCurrentUrl()).getPath().endsWith(urlEndsWithPath)
                        && ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
                );
    }

    private void clickOnButton(WebDriver driver, By identifier) {
        clickOnButton(driver, identifier, false);
    }

    private void clickOnButton(WebDriver driver, By identifier, boolean allowReachedErrorPage) {
        withRetry.execute(context -> {
            if (allowReachedErrorPage) {
                swallowReachedErrorPageException(() -> performClick(driver, identifier));
            } else {
                performClick(driver, identifier);
            }

            return null;
        });
    }

    private void performClick(WebDriver driver, By identifier) {
        wait(driver).until(ExpectedConditions.elementToBeClickable(identifier));
        driver.findElement(identifier).click();
    }

    private void sendText(WebDriver driver, By identifier, String text) {
        withRetry.execute(context -> {
            wait(driver).until(ExpectedConditions.elementToBeClickable(identifier));
            driver.findElement(identifier).sendKeys(text);
            return null;
        });
    }

    private void selectByVisibleInDropdown(WebDriver driver, By id, String visibleText) {
        withRetry.execute(context -> {
            wait(driver).until(ExpectedConditions.elementToBeClickable(id));
            Select elem = new Select(driver.findElement(id));
            elem.selectByVisibleText(visibleText);
            return null;
        });
    }

    private void swallowReachedErrorPageException(Runnable action) {
        try {
            action.run();
        } catch (WebDriverException ex) {
            if (null != ex.getMessage() && ex.getMessage().contains("Reached error page")) {
                return;
            }

            throw ex;
        }
    }
}
