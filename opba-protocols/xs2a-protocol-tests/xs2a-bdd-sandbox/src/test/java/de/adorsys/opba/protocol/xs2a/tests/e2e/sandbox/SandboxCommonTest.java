package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.security.Security;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.awaitility.Awaitility.await;

public class SandboxCommonTest<GIVEN, WHEN, THEN> extends SpringScenarioTest<GIVEN, WHEN, THEN> {

    protected static final LocalDate DATE_FROM = LocalDate.now().minusYears(1);
    protected static final LocalDate DATE_TO = LocalDate.now();
    protected static final String BOTH_BOOKING = "BOTH";

    protected final String OPBA_LOGIN = UUID.randomUUID().toString();
    protected final String OPBA_PASSWORD = UUID.randomUUID().toString();

    private static SandboxOper SANDBOX;

    @LocalServerPort
    protected int port;

    @Autowired
    protected ProtocolUrlsConfiguration urlsConfiguration;

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
        private static final String DOCKER_COMPOSE_SANDBOX_YML = "./../../../how-to-start-with-project/xs2a-sandbox-only/docker-compose.yml";
        private static final ObjectMapper YML = new ObjectMapper(new YAMLFactory());
        private static final int MAX_WAIT_MINUTES = 10;

        private DockerComposeContainer sandboxEnvironment;

        @SneakyThrows
        synchronized void startSandbox() {
            if (null != sandboxEnvironment) {
                throw new IllegalStateException("Sandbox env is up, in needs to be shut down first");
            }

            // while it is not launching anything, it allows to await for apps to be ready to use:
            sandboxEnvironment = new DockerComposeContainer(new File(DOCKER_COMPOSE_SANDBOX_YML))
                    .withLocalCompose(true)
                    .withTailChildContainers(true);
            sandboxEnvironment.start();
            awaitAllStarted();
        }


        @SneakyThrows
        synchronized void stopSandbox() {
            sandboxEnvironment.stop();
            awaitAllStopped();
            sandboxEnvironment = null;
        }

        private void awaitAllStarted() {
            int[] portsToCheck = getDockerServicesPorts();

            await().atMost(Duration.ofMinutes(MAX_WAIT_MINUTES)).until(
                    () -> Arrays.stream(portsToCheck).map(port -> isPortListening(port) ? 1 : 0).sum() == portsToCheck.length
            );
        }

        @SneakyThrows
        private int[] getDockerServicesPorts() {
            JsonNode appConfig = YML.readTree(new File(DOCKER_COMPOSE_SANDBOX_YML));
            var dockerServices = appConfig.at("/services");
            return StreamSupport.stream(dockerServices.spliterator(), false)
                    .map(it -> Integer.parseInt(it.at("/ports/0").asText().split(":")[0]))
                    .mapToInt(Integer::intValue)
                    .toArray();
        }

        private void awaitAllStopped() {
            int[] portsToCheck = getDockerServicesPorts();

            await().atMost(Duration.ofMinutes(MAX_WAIT_MINUTES)).until(
                    () -> Arrays.stream(portsToCheck).map(port -> isPortListening(port) ? 1 : 0).sum() == 0
            );
        }

        private boolean isPortListening(int port) {
            try (Socket ignored = new Socket("localhost", port)) {
                // Deeper check for docker-compose runtime as docker opens port always, Java-based stuff can simply return true
                URL url = new URL("http://localhost:" + port);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                int statusCode = http.getResponseCode();
                return statusCode >= HttpStatus.OK.value(); // Assuming that if we got such status code app should be more or less ready
            } catch (IOException ignored) {
                return false;
            }
        }
    }
}
