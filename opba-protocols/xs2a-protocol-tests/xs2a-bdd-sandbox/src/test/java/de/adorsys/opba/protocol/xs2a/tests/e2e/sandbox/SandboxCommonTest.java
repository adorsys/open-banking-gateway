package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.testsandbox.SandboxAppsStarter;
import de.adorsys.opba.protocol.xs2a.testsandbox.internal.SandboxApp;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.io.File;
import java.security.Security;
import java.time.LocalDate;
import java.util.UUID;

public class SandboxCommonTest<GIVEN, WHEN, THEN> extends SpringScenarioTest<GIVEN, WHEN, THEN> {

    protected static final LocalDate DATE_FROM = LocalDate.now().minusYears(1);
    protected static final LocalDate DATE_TO = LocalDate.now();
    protected static final String BOTH_BOOKING = "BOTH";

    protected final String OPBA_LOGIN = UUID.randomUUID().toString();
    protected final String OPBA_PASSWORD = UUID.randomUUID().toString();

    private static SandboxOper SANDBOX;

    @LocalServerPort
    private int port;

    @Autowired
    private ProtocolUrlsConfiguration urlsConfiguration;

    @BeforeAll
    @SneakyThrows
    static void startSandbox() {
        WebDriverManager.firefoxdriver().arch64();

        if (null != System.getenv("NO_SANDBOX_START")) {
            return;
        }

        if (null != SANDBOX) {
            throw new IllegalStateException("Sandbox should be null (stopped)");
        }
        SANDBOX = new SandboxOper();
        SANDBOX.startSandbox();

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
    }

    @AfterAll
    static void stopSandbox() {
        SANDBOX.stopSandbox();
        SANDBOX = null;
    }

    // See https://github.com/spring-projects/spring-boot/issues/14879 for the 'why setting port'
    @BeforeEach
    void setBaseUrl() {
        ProtocolUrlsConfiguration.WebHooks aisUrls = urlsConfiguration.getAis().getWebHooks();
        aisUrls.setOk(aisUrls.getOk().replaceAll("localhost:\\d+", "localhost:" + port));
        aisUrls.setNok(aisUrls.getNok().replaceAll("localhost:\\d+", "localhost:" + port));

        ProtocolUrlsConfiguration.WebHooks pisUrls = urlsConfiguration.getPis().getWebHooks();
        pisUrls.setOk(pisUrls.getOk().replaceAll("localhost:\\d+", "localhost:" + port));
        pisUrls.setNok(pisUrls.getNok().replaceAll("localhost:\\d+", "localhost:" + port));
    }

    private static class SandboxOper {
        private static final ObjectMapper YML = new ObjectMapper(new YAMLFactory());
        private static final String DOCKER_COMPOSE_SANDBOX_YML = "./../../../how-to-start-with-project/xs2a-sandbox-only/docker-compose.yml";

        private SandboxAppsStarter starter;
        private DockerComposeContainer sandboxEnvironment;

        @SneakyThrows
        synchronized void startSandbox() {
            if (null != sandboxEnvironment) {
                throw new IllegalStateException("Sandbox env is up, in needs to be shut down first");
            }

            // push Online-Banking-UI declared port as we use Starter only to check if all apps are up
            JsonNode appConfig = YML.readTree(new File(DOCKER_COMPOSE_SANDBOX_YML));
            int onlineBankingUiPort = Integer.parseInt(appConfig.at("/services/x2sa-sandbox-onlinebankingui/ports/0").asText().split(":")[0]);
            // while it is not launching anything, it allows to await for apps to be ready to use:
            starter = new SandboxAppsStarter(ImmutableMap.of(SandboxApp.ONLINE_BANKING_UI, onlineBankingUiPort));
            // Ensure that ports are clear:
            starter.awaitForAllStopped();

            sandboxEnvironment = new DockerComposeContainer(new File(DOCKER_COMPOSE_SANDBOX_YML))
                    .withLocalCompose(true)
                    .withTailChildContainers(true);
            sandboxEnvironment.start();
            starter.awaitForAllStarted();
        }


        @SneakyThrows
        synchronized void stopSandbox() {
            sandboxEnvironment.stop();
            starter.awaitForAllStopped();
            sandboxEnvironment = null;
        }
    }
}
