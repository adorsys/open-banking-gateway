package de.adorsys.fintech.tests.e2e;

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

import java.time.Duration;

import static de.adorsys.fintech.tests.e2e.FintechStagesUtils.PIN_VALUE;
import static de.adorsys.fintech.tests.e2e.FintechStagesUtils.USERNAME;
import static de.adorsys.fintech.tests.e2e.config.RetryableConfig.TEST_RETRY_OPS;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WebDriverBasedUserInfoFintech<SELF extends WebDriverBasedUserInfoFintech<SELF>> extends Stage <SELF> {

    @Autowired
    @Qualifier(TEST_RETRY_OPS)
    private RetryOperations withRetry;

    @Value("${test.fintech.server-uri}")
    private  String fintechUri;

    @ProvidedScenarioState
    private String redirectURI;

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    public SELF user_opens_fintechui_login_page(WebDriver driver) {
        driver.get(fintechUri);
        return self();
    }

    public SELF user_login_with_its_credentials(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.name("Username"));
        sendText(driver, By.name("Username"), USERNAME);
        sendText(driver, By.name("Password"), PIN_VALUE);
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF user_navigates_to_bank_search(WebDriver driver) {
        driver.get(redirectURI);
        return self();
    }

    public SELF user_look_for_a_bank_in_the_bank_search_input_place(WebDriver driver) {
        waitForPageLoad(driver);
        sendText(driver, By.name("Search"), "adorsys xs2a");
        clickOnButton(driver, By.xpath("//button[@type='submit']"));
        return self();
    }

    public SELF user_naviagtes_to_bank_profile(WebDriver webDriver) {
        webDriver.get(redirectURI);
        return self();
    }

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
