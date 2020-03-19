package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryOperations;

import java.time.Duration;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WebDriverBasedAccountInformation<SELF extends WebDriverBasedAccountInformation<SELF>> extends AccountInformationRequestCommon<SELF> {

    @Autowired
    private RetryOperations withRetry;

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    public SELF user_anton_brueckner_opens_opba_consent_auth_entry_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        return self();
    }

    public SELF user_anton_brueckner_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_consent(WebDriver driver) {
        waitForPageLoad(driver);
        sendText(driver, By.id("PSU_ID"), "anton.brueckner");
        clickOnButton(driver, By.id("ALL_ACCOUNTS_WITH_BALANCES"));
        clickOnButton(driver, By.id("do_submit"));
        return self();
    }

    public SELF user_anton_brueckner_provided_to_consent_ui_initial_parameters_to_list_accounts_with_all_accounts_transactions_consent(WebDriver driver) {
        waitForPageLoad(driver);
        sendText(driver, By.id("PSU_ID"), "anton.brueckner");
        clickOnButton(driver, By.id("ALL_PSD2"));
        clickOnButton(driver, By.id("do_submit"));
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_reviews_accounts_concent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEnds(driver, "entry-consent-accounts/review-consent");
        clickOnButton(driver, By.id("do_submit"));
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_sees_redirection_info_to_aspsp_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEnds(driver, "/to-aspsp-redirection");
        clickOnButton(driver, By.id("do_submit"));
        return self();
    }

    public SELF sandbox_anton_brueckner_from_consent_ui_navigates_to_bank_auth_page(WebDriver driver) {
        waitForPageLoadAndUrlContains(driver, "account-information/login");
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_sees_thank_you_for_consent_and_clicks_to_tpp(WebDriver driver) {
        waitForPageLoadAndUrlContains(driver, "/consent-result");
        clickOnButton(driver, By.id("do_submit"));
        return self();
    }

    public SELF user_anton_brueckner_in_consent_ui_reviews_transaction_consent_and_accepts(WebDriver driver) {
        waitForPageLoadAndUrlEnds(driver, "entry-consent-transactions/review-consent");
        clickOnButton(driver, By.id("do_submit"));
        return self();
    }

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

    private void waitForPageLoadAndUrlEnds(WebDriver driver, String urlEndsWith) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd ->
                        driver.getCurrentUrl().endsWith(urlEndsWith)
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
