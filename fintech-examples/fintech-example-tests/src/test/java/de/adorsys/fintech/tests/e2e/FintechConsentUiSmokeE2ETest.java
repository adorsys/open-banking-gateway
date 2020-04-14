package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.fintech.tests.e2e.config.SmokeConfig;
import de.adorsys.fintech.tests.e2e.steps.FintechServer;
import de.adorsys.fintech.tests.e2e.steps.WebDriverBasedUserInfoFintech;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
public class FintechConsentUiSmokeE2ETest extends SpringScenarioTest<FintechServer, WebDriverBasedUserInfoFintech<? extends WebDriverBasedUserInfoFintech<?>>, AccountInformationResult> {

    @Autowired
    private SmokeConfig smokeConfig;

    @BeforeAll
    static void setupDriverArch() {
        WebDriverManager.firefoxdriver().arch64();
    }

    @Test
    public void testUserAuthorizeConsentFromFintechUI(FirefoxDriver firefoxDriver) {

    }
}
