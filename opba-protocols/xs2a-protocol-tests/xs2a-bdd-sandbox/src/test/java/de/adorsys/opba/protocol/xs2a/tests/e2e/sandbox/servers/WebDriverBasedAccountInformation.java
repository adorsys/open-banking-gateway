package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import org.flowable.engine.RuntimeService;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryOperations;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

@JGivenStage
public class WebDriverBasedAccountInformation<SELF extends WebDriverBasedAccountInformation<SELF>> extends AccountInformationRequestCommon<SELF> {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RetryOperations withRetry;

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    public SELF sandbox_anton_brueckner_navigates_to_bank_auth_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        return self();
    }

    public SELF sandbox_anton_brueckner_inputs_username_and_password(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.name("login"));
        sendText(driver, By.name("login"), "anton.brueckner");
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

    public SELF sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button(WebDriver driver) {
        waitForPageLoad(driver);
        clickOnButton(driver, By.className("btn-primary"));
        return self();
    }

    private WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, timeout.getSeconds());
    }

    private void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    private void clickOnButton(WebDriver driver, By identifier) {
        withRetry.execute(context -> {
            swallowReachedErrorPagException(() -> {
                wait(driver).until(elementToBeClickable(identifier));
                driver.findElement(identifier).click();
            });
            return null;
        });
    }

    private void sendText(WebDriver driver, By identifier, String text) {
        withRetry.execute(context -> {
            wait(driver).until(elementToBeClickable(identifier));
            driver.findElement(identifier).sendKeys(text);
            return null;
        });
    }

    private void swallowReachedErrorPagException(Runnable action) {
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
