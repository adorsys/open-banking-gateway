package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import net.bytebuddy.utility.RandomString;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryOperations;

import java.net.URI;
import java.time.Duration;

import static de.adorsys.fintech.tests.e2e.config.RetryableConfig.TEST_RETRY_OPS;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.PIN;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.USERNAME;


@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WebDriverBasedUserInfoFintech<SELF extends WebDriverBasedUserInfoFintech<SELF>> extends WebDriverBasedAccountInformation<SELF> {

    static final String FINTECH_URI = "https://obg-dev-fintechui.cloud.adorsys.de";

    @Autowired
    @Qualifier(TEST_RETRY_OPS)
    private RetryOperations withRetry;

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    public SELF user_opens_fintechui_login_page(WebDriver driver) {
        driver.get(FINTECH_URI);
        return self();
    }

    public SELF user_sees_account_and_list_transactions(WebDriver webDriver) {
        wait(webDriver);
        performClick(webDriver, By.className("lacc-list-container"));
        waitPlusTimer(webDriver, 20);
        return self();
    }

    public SELF sandbox_max_musterman_inputs_username_and_password(WebDriver driver) {
        waitPlusTimer(driver, timeout.getSeconds());
        clickOnButton(driver, By.name("login"));
        sendText(driver, By.name("login"), MAX_MUSTERMAN);
        sendText(driver, By.name("pin"), "12345");
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_max_musterman_provides_sca_challenge_result(WebDriver driver) {
        waitPlusTimer(driver, timeout.getSeconds());
        sendText(driver, By.name("authCode"), "123456");
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF sandbox_max_musterman_from_consent_ui_navigates_to_bank_auth_page(WebDriver driver) {
        waitForPageLoadAndUrlContains(driver, "account-information/login");
        return self();
    }

    public SELF user_in_consent_ui_sees_redirection_info_to_aspsp_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "/to-aspsp-redirection");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_sees_sca_select_and_confirm_type_email2_to_redirect_authorization(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "select-sca");
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(WebDriver driver) {
        wait(driver);
        clickOnButton(driver, By.className("btn-primary"), true);
        return self();
    }

    public SELF user_max_musterman_in_consent_ui_reviews_transaction_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions/review-consent");
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_max_musterman_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_transactions_consent(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "entry-consent-transactions");
        sendText(driver, By.id("PSU_ID"), MAX_MUSTERMAN);
        clickOnButton(driver, By.id("ALL_PSD2"));
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_login_with_its_credentials(WebDriver driver) {
        sendText(driver, By.id("username"), USERNAME + RandomString.make().toLowerCase());
        sendText(driver, By.id("password"), PIN);
        return self();
    }

    public SELF user_wait_for_the_result_in_bank_search(WebDriver webDriver) {
        wait(webDriver);
        clickOnButton(webDriver, By.className("bank-list"), false);
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_provides_pin(WebDriver driver) {
        wait(driver);
        sendText(driver, By.xpath("//button[@type='password']"), PIN_VALUE);
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_provides_sca_result_to_embedded_authorization(WebDriver driver) {
        waitForPageLoadAndUrlEndsWithPath(driver, "sca-result");
        sendText(driver, By.id("tan"), TAN_VALUE);
        clickOnButton(driver, By.id(SUBMIT_ID));
        return self();
    }

    public SELF user_already_login_in_bank_profile(WebDriver firefoxDriver) {
        user_opens_fintechui_login_page(firefoxDriver)
                .and()
                .user_login_with_its_credentials(firefoxDriver)
                .and()
                .user_confirm_login(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_looks_for_a_bank_in_the_bank_search_input_place(firefoxDriver)
                .and()
                .user_wait_for_the_result_in_bank_search(firefoxDriver)
                .and()
                .user_navigates_to_page(firefoxDriver)
                .and()
                .user_select_account_button(firefoxDriver);
        return self();
    }

    public SELF user_back_to_bank_search(WebDriver webDriver) {
        wait(webDriver);
        performClick(webDriver, By.linkText("Bank search"));
        return self();
    }


    public SELF user_navigates_to_page(WebDriver driver) {
        wait(driver);
        return self();
    }

    public SELF user_looks_for_a_bank_in_the_bank_search_input_place(WebDriver driver) {
        wait(driver);
        sendTestInSearchInput(driver, By.name("searchValue"), " xs2a");
        return self();
    }

    public SELF user_after_login_wants_to_logout(WebDriver webDriver) {
        wait(webDriver);
        clickOnButton(webDriver, By.id("dropdownMenuButton"));
        return  self();
    }

    public SELF user_click_on_logout_button(WebDriver webDriver) {
        wait(webDriver);
        clickOnButton(webDriver, By.className("nav__title"), false);
        return self();
    }

    public SELF user_confirm_login(WebDriver webDriver) {
        wait(webDriver);
        clickOnButton(webDriver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF user_accepts_to_get_redirected_to_consentui(WebDriver webDriver) {
        wait(webDriver);
        performClick(webDriver, By.xpath("//button[@class='btn btn-primary btn-center']"));
        return self();
    }

    public SELF user_select_account_button(WebDriver webDriver) {
        performClick(webDriver, By.linkText("Accounts"));
        return self();
    }

    //TODO add stages for accountList and TransactionList

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

    private WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, timeout.getSeconds());
    }

    private WebDriverWait waitPlusTimer(WebDriver webDriver, long duration) {
        return new WebDriverWait(webDriver, timeout.getSeconds() + duration);
    }

    private void sendTestInSearchInput(WebDriver driver, By id, String visibleText) {
        withRetry.execute(context -> {
            wait(driver).until(ExpectedConditions.elementToBeClickable(id));
            WebElement input = driver.findElement(id);
            input.click();
            input.sendKeys(visibleText);
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

    private void waitForPageLoadAndUrlEndsWithPath(WebDriver driver, String urlEndsWithPath) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd ->
                               URI.create(driver.getCurrentUrl()).getPath().endsWith(urlEndsWithPath)
                                       && ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
                );
    }

    private void waitForPageLoadAndUrlContains(WebDriver driver, String urlContains) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd ->
                               driver.getCurrentUrl().contains(urlContains)
                                       && ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
                );
    }

}
