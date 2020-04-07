package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.fintech.tests.e2e.config.SmokeConfig;
import de.adorsys.fintech.tests.e2e.steps.FintechServer;
import de.adorsys.fintech.tests.e2e.steps.UserInformationResult;
import de.adorsys.fintech.tests.e2e.steps.WebDriverBasedUserInfoFintech;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ActiveProfiles("test-mocked-fintech")
@ExtendWith(SeleniumExtension.class)
@SpringBootTest(classes = JGivenConfig.class, webEnvironment = NONE)
class FintechApiSmokeTest extends SpringScenarioTest<FintechServer, WebDriverBasedUserInfoFintech<? extends WebDriverBasedUserInfoFintech<?>>, UserInformationResult> {

    @Autowired
    private SmokeConfig smokeConfig;

    @BeforeAll
    static void setupDriverArch() {
        WebDriverManager.firefoxdriver().arch64();
    }

    @Test
    void testUserLoginToFintech(FirefoxDriver firefoxDriver) {
        given().fintech_points_to_fintechui_login_page(smokeConfig.getFintechServerUri());
        when().user_opens_fintechui_login_page(firefoxDriver)
                .and()
                .user_login_with_its_credentials(firefoxDriver);
        then().fintech_can_read_user_data_using_xsrfToken();
    }

    @Test
    void testUserSearchesABank(FirefoxDriver firefoxDriver) {
        given().fintech_point_to_another_page(firefoxDriver, "search");
        when().user_opens_fintechui_login_page(firefoxDriver)
              .and()
              .user_sees_that_does_not_need_to_login(firefoxDriver);

        UserInformationResult result = then().fintech_can_read_bank_profile_using_xsrfToken("adorsys xs2a");
    }

}
