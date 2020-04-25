package de.adorsys.opba.protocol.xs2a.testsandbox;

import de.adorsys.opba.protocol.xs2a.testsandbox.internal.SandboxApp;
import de.adorsys.opba.protocol.xs2a.testsandbox.internal.StarterContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static de.adorsys.opba.protocol.xs2a.testsandbox.Const.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.StringContains.containsString;


@Slf4j
@EnabledIfEnvironmentVariable(named = ENABLE_HEAVY_TESTS, matches = TRUE_BOOL)
class BasicTest extends BaseMockitoTest {

    private static final SandboxAppsStarter executor = new SandboxAppsStarter();
    private static StarterContext ctx;

    @BeforeAll
    static void startSandbox() {
        ctx = executor.runAll();
        executor.awaitForAllStarted();
    }

    @AfterAll
    static void stopSandbox() {
        executor.shutdown();
    }

    /**
     * Sanity test that validates E2E-Sandbox can run on platform.
     */
    @Test
    @SneakyThrows
    void testEnvStartsUp() {
        executor.awaitForAllStarted();

        assertThat(SandboxApp.values()).extracting(it -> ctx.getLoader().get(it)).isNotNull();
        // Dockerized UI can reach backend
        given().when()
            .post("http://localhost:4400/oba-proxy/ais/FOO=BAR/authorisation/12345/login?pin=12345")
            .then()
            .body(containsString("Internal Server Error"))
            .statusCode(500);
    }

    /**
     * Not really a test, but just launches entire sandbox for you.
     * If run with intellij 2018.3 please set VM option to -DSTART_SANDBOX=true and environment-variable ENABLE_HEAVY_TESTS=true
     */
    @Test
    @SneakyThrows
    @EnabledIfSystemProperty(named = "START_SANDBOX", matches = TRUE_BOOL)
    void startTheSandbox() {
        executor.awaitForAllStarted();

        assertThat(SandboxApp.values()).extracting(it -> ctx.getLoader().get(it)).isNotNull();

        // Loop forever.
        await().forever().until(() -> false);
    }
}
