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

import java.net.URI;
import java.time.Duration;

import static de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.config.RetryableConfig.TEST_RETRY_OPS;
import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_KEY;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WebDriverBasedAccountInformation<SELF extends WebDriverBasedAccountInformation<SELF>> extends AccountInformationRequestCommon<SELF> {

    public static final String SUBMIT_ID = "do_submit";
    public static final String ANTON_BRUECKNER = "anton.brueckner";
    public static final String MAX_MUSTERMAN = "max.musterman";
    public static final String MAX_MUSTERMAN_IBAN = "DE38760700240320465700";
    public static final String PIN_VALUE = "12345";
    public static final String TAN_VALUE = "123456";

    @Autowired
    @Qualifier(TEST_RETRY_OPS)
    private RetryOperations withRetry;

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    public SELF user_anton_brueckner_opens_opba_consent_login_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        waitForPageLoadAndUrlContains(driver, "/login");
        return self();
    }

    public SELF user_max_musterman_opens_opba_consent_login_page(WebDriver driver) {
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

    public SELF user_anton_brueckner_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts");
        sendText(driver, By.id("PSU_ID"), ANTON_BRUECKNER);
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

    public SELF user_max_musterman_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts");
        sendText(driver, By.id("PSU_ID"), MAX_MUSTERMAN);
        clickOnButton(driver, By.id("ALL_ACCOUNTS"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }


    public SELF user_max_musterman_provided_to_consent_ui_initial_parameters_to_list_accounts_with_dedicated_transactions_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions");
        sendText(driver, By.id("PSU_ID"), MAX_MUSTERMAN);
        clickOnButton(driver, By.id("FINE_GRAINED"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_provided_to_consent_ui_initial_parameters_to_list_accounts_with_dedicated_accounts_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts");
        sendText(driver, By.id("PSU_ID"), MAX_MUSTERMAN);
        clickOnButton(driver, By.id("FINE_GRAINED"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_provided_to_consent_ui_account_iban_for_dedicated_accounts_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts/dedicated-account-access");
        sendText(driver, By.cssSelector("[id^=account-reference]"), MAX_MUSTERMAN_IBAN);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_provided_to_consent_ui_account_iban_for_dedicated_transactions_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions/dedicated-account-access");
        sendText(driver, By.cssSelector("[id^=account-reference]"), MAX_MUSTERMAN_IBAN);
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

    public SELF user_anton_brueckner_in_consent_ui_reviews_accounts_concent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts/review-consent");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_sees_redirection_info_to_aspsp_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "/to-aspsp-redirection");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF sandbox_anton_brueckner_from_consent_ui_navigates_to_bank_auth_page(WebDriver driver) {
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

    public SELF user_anton_brueckner_in_consent_ui_reviews_transaction_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions/review-consent");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_reviews_account_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-accounts/review-consent");
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

    public SELF user_max_musterman_in_consent_ui_provides_sca_result_to_embedded_authorization(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "sca-result");
        sendText(driver, By.id("tan"), TAN_VALUE);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_sees_sca_select_and_selected_type_email2_to_embedded_authorization(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "select-sca-method");
        selectByVisibleInDropdown(driver, By.id("scaMethod"), "EMAIL:max.musterman2@mail.de");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF sandbox_anton_brueckner_navigates_to_bank_auth_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        return self();
    }

    public SELF sandbox_anton_brueckner_inputs_username_and_password(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.name("login"));
        sendText(driver, By.name("login"), ANTON_BRUECKNER);
        sendText(driver, By.name("pin"), "12345");
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_anton_brueckner_confirms_consent_information(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_anton_brueckner_selects_sca_method(WebDriver driver) {
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

    // Sending cookie with last request as it doesn't exist in browser for API tests
    public SELF sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_only(WebDriver driver) {
        waitForPageLoad(driver);
        driver.manage().addCookie(new Cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie));
        try {
            clickOnButton(driver, By.className("btn-primary"), true);
        } finally {
            driver.manage().deleteCookieNamed(AUTHORIZATION_SESSION_KEY);
        }
        return self();
    }

    public SELF sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.className("btn-primary"), true);
        return self();
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
