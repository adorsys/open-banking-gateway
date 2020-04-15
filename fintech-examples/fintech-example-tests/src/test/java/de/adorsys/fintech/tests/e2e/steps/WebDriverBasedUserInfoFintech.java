package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryOperations;

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
        performClick(webDriver, By.className("lacc-list-item__headline"));
        wait(webDriver);
        return self();
    }

    public SELF user_login_with_its_credentials(WebDriver driver) {
        sendText(driver, By.id("username"), USERNAME);
        sendText(driver, By.id("password"), PIN);
        return self();
    }

    public SELF user_wait_for_the_result_in_bank_search(WebDriver webDriver) {
        wait(webDriver);
        clickOnButton(webDriver, By.className("bank-list"), false);
        return self();
    }

    public SELF user_already_login_in_bank_profile(FirefoxDriver firefoxDriver) {
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


    public SELF user_navigates_to_page(WebDriver driver) {
        wait(driver);
        return self();
    }

    public SELF user_looks_for_a_bank_in_the_bank_search_input_place(WebDriver driver) {
        wait(driver);
        sendTestInSearchInput(driver, By.name("searchValue"), " xs2a");
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
}
