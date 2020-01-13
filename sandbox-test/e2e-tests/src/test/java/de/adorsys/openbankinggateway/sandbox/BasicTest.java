package de.adorsys.openbankinggateway.sandbox;

import de.sandbox.openbankinggateway.sandbox.SandboxAppsStarter;
import de.sandbox.openbankinggateway.sandbox.internal.SandboxApp;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.StringContains.containsString;


@Slf4j
class BasicTest extends BaseMockitoTest {

    private final SandboxAppsStarter executor = new SandboxAppsStarter();

    /**
     * Sanity test that validates E2E-Sandbox can run on platform.
     */
    @Test
    @SneakyThrows
    void testEnvStartsUp() {
        executor.runAll();
        executor.awaitForAllStarted();

        assertThat(SandboxApp.values()).extracting(it -> it.getLoader().get()).isNotNull();
        // Dockerized UI can reach backend
        given().when()
            .post("http://localhost:4400/oba-proxy/ais/FOO=BAR/authorisation/12345/login?pin=12345")
            .then()
            .body(containsString("Internal Server Error"))
            .statusCode(500);
    }

    /**
     * Not really a test, but just launches entire sandbox for you.
     */
    @Test
    @SneakyThrows
    @EnabledIfSystemProperty(named = "START_SANDBOX", matches = "true")
    void startTheSandbox() {
        executor.runAll();
        executor.awaitForAllStarted();

        assertThat(SandboxApp.values()).extracting(it -> it.getLoader().get()).isNotNull();

        // Loop forever.
        await().forever().until(() -> false);
    }
}
