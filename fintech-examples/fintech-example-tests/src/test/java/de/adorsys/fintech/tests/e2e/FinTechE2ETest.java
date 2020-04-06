package de.adorsys.fintech.tests.e2e;

import de.adorsys.fintech.tests.e2e.config.RetryableConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.security.Security;

import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;

@ExtendWith(SeleniumExtension.class)
@EnableAutoConfiguration()
@SpringBootTest(classes = {RetryableConfig.class, JGivenConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS})
//public class FinTechE2ETest extends SpringScenarioTest<FintechServer, WebDriverBasedUserInfoFintech<? extends WebDriverBasedUserInfoFintech<?>>, UserInformationResult> {
public class FinTechE2ETest {

    @LocalServerPort
    private int port;

    @BeforeAll
    static void BeforeFintechStarts() {
        WebDriverManager.firefoxdriver().arch64();
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
    }

}
