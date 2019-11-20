package de.adorsys.openbankinggateway;

import de.adorsys.openbankinggateway.sandbox.SandboxAppsStarter;
import de.adorsys.openbankinggateway.sandbox.internal.SandboxApp;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@Slf4j
class BasicTest extends WithSandboxSpringBootTest {

    private final SandboxAppsStarter executor = new SandboxAppsStarter();

    @Test
    @SneakyThrows
    void testEnvStartsUp() {
        executor.runAll();

        await().atMost(Duration.FIVE_MINUTES).pollDelay(Duration.ONE_SECOND).until(SandboxApp::allReadyToUse);
        log.info("ALL READY!");

        Thread.sleep(20000);
        assertThat(SandboxApp.values()).extracting(it -> it.getLoader().get()).isNotNull();
    }
}
