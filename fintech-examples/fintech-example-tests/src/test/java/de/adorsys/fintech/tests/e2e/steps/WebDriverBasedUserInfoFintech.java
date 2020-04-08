package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.openqa.selenium.By;
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

import static de.adorsys.fintech.tests.e2e.config.RetryableConfig.TEST_RETRY_OPS;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.*;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WebDriverBasedUserInfoFintech<SELF extends WebDriverBasedUserInfoFintech<SELF>> extends Stage<SELF> {

    static final String FINTECH_URI = "https://obg-dev-fintechui.cloud.adorsys.de";

    @Autowired
    @Qualifier(TEST_RETRY_OPS)
    private RetryOperations withRetry;

    @Value("${test.fintech.uri}")
    private  String fintechUri;

    @ProvidedScenarioState
    private String redirectURI;

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    public SELF user_opens_fintechui_login_page(WebDriver driver) {
        driver.get(FINTECH_URI);
        return self();
    }

    public SELF user_login_with_its_credentials(WebDriver driver) {
        sendText(driver, By.id("username"), USERNAME);
        sendText(driver, By.id("password"), PIN_VALUE);
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF user_sees_that_does_not_need_to_login(WebDriver webDriver) {
        waitForPageLoadAndUrlEndsWithPath(webDriver, "search");
        return self();
    }

    public SELF user_back_to_bank_search(WebDriver webDriver) {
        waitForPageLoad(webDriver);
        clickOnButton(webDriver, By.className("icon__container"), true);
        return self();
    }

    public SELF user_sees_that_he_has_to_login(WebDriver webDriver) {
        webDriver.get(FINTECH_URI);
        return self();
    }

    public SELF user_navigates_to_bank_search(WebDriver driver) {
        wait(driver);
        return self();
    }

    public SELF user_looks_for_a_bank_in_the_bank_search_input_place(WebDriver driver) {
        waitForPageLoad(driver);
        sendText(driver, By.id("search"), "adorsys xs2a");
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF user_confirm_login(WebDriver webDriver) {
        waitForPageLoad(webDriver);
        clickOnButton(webDriver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF user_naviagtes_to_bank_profile(WebDriver webDriver) {
        waitForPageLoadAndUrlEndsWithPath(webDriver, "bank" + BANK_ID_VALUE);
        return self();
    }

    //TODO add stages for accountList and TransactionList

    private void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
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

    private WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, timeout.getSeconds());
    }

    private void selectByVisibleInDropdown(WebDriver driver, By id, String visibleText) {
        withRetry.execute(context -> {
            wait(driver).until(ExpectedConditions.elementSelectionStateToBe(id, true));
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

    private void waitForPageLoadAndUrlEndsWithPath(WebDriver driver, String urlEndsWithPath) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd ->
                        URI.create(driver.getPageSource()).getPath().endsWith(urlEndsWithPath)
                                && ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
                );
    }

}
