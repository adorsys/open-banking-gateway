package de.adorsys.opba.core.protocol.e2e.stages.real;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.core.protocol.e2e.stages.AccountInformationRequestCommon;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import org.flowable.engine.RuntimeService;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

@JGivenStage
public class WebDriverBasedAccountInformation<SELF extends WebDriverBasedAccountInformation<SELF>> extends AccountInformationRequestCommon<SELF> {

    private static final int WAIT_TIMEOUT_S = 10;

    @Autowired
    private RuntimeService runtimeService;

    public SELF sandbox_anton_brueckner_navigates_to_bank_auth_page(WebDriver driver) {
        driver.get(redirectUriToGetUserParams);
        return self();
    }

    public SELF sandbox_anton_brueckner_inputs_username_and_password(WebDriver driver) {
        waitForLoad(driver);
        wait(driver).until(elementToBeClickable(By.name("login")));
        driver.findElement(By.name("login")).sendKeys("anton.brueckner");
        driver.findElement(By.name("pin")).sendKeys("12345");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        return self();
    }

    public SELF sandbox_anton_brueckner_confirms_consent_information(WebDriver driver) {
        waitForLoad(driver);
        wait(driver).until(elementToBeClickable(By.xpath("//button[@type='submit']")));
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        return self();
    }

    public SELF sandbox_anton_brueckner_selects_sca_method(WebDriver driver) {
        waitForLoad(driver);
        wait(driver).until(elementToBeClickable(By.xpath("//button[@type='submit']")));
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        return self();
    }

    public SELF sandbox_anton_brueckner_provides_sca_challenge_result(WebDriver driver) {
        waitForLoad(driver);
        wait(driver).until(elementToBeClickable(By.name("authCode")));
        driver.findElement(By.name("authCode")).sendKeys("123456");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        return self();
    }

    public SELF sandbox_anton_brueckner_see_redirect_back_to_tpp_button(WebDriver driver) {
        waitForLoad(driver);
        wait(driver).until(elementToBeClickable(By.className("btn-primary")));
        String waitingExecutionId = runtimeService.createActivityInstanceQuery()
                .unfinished()
                .orderByActivityInstanceStartTime()
                .desc()
                .list()
                .get(0)
                .getExecutionId();

        Xs2aContext ctx = (Xs2aContext) runtimeService.getVariable(waitingExecutionId, CONTEXT);
        this.redirectOkUri = ctx.getRedirectUriOk();
        return self();
    }

    private static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, WAIT_TIMEOUT_S);
    }

    private static void waitForLoad(WebDriver driver) {
        new WebDriverWait(driver, 30)
                .until(wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }
}
