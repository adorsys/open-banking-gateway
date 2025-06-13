package de.adorsys.opba.protocol.xs2a.testsandbox;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

import static de.adorsys.opba.protocol.xs2a.testsandbox.Const.ENABLE_HEAVY_TESTS;
import static de.adorsys.opba.protocol.xs2a.testsandbox.Const.TRUE_BOOL;
import static org.awaitility.Awaitility.await;

@Slf4j
@EnabledIfEnvironmentVariable(named = ENABLE_HEAVY_TESTS, matches = TRUE_BOOL)
class BasicTest extends BaseMockitoTest {

    /**
     * Not really a test, but just launches entire sandbox for you.
     * If run with intellij 2018.3 please set VM option to -DSTART_SANDBOX=true and environment-variable ENABLE_HEAVY_TESTS=true
     */
    @Test
    @SneakyThrows
    @EnabledIfSystemProperty(named = "START_SANDBOX", matches = TRUE_BOOL)
    void startTheSandboxInDocker() {
        DockerComposeContainer environment = new DockerComposeContainer(new File("./../../../how-to-start-with-project/xs2a-sandbox-only/docker-compose.yml"))
                                                     .withLocalCompose(true)
                                                     .withTailChildContainers(true);
        environment.start();

        // Loop forever.
        await().forever().until(() -> false);
    }

    /**
     * Not really a test, but just launches Wiremock, with Sandbox mocking fixtures for you.
     * If run with intellij 2018.3 please set VM option to -DSTART_SANDBOX=true and environment-variable ENABLE_HEAVY_TESTS=true
     */
    @Test
    @SneakyThrows
    @EnabledIfSystemProperty(named = "START_SANDBOX", matches = TRUE_BOOL)
    void startTheWiremockInDocker() {
        DockerComposeContainer environment = new DockerComposeContainer(new File("./src/main/resources/docker-compose-with-wiremock.yml"))
                                                     .withLocalCompose(true)
                                                     .withTailChildContainers(true);
        environment.start();

        // Loop forever.
        await().forever().until(() -> false);
    }
}
