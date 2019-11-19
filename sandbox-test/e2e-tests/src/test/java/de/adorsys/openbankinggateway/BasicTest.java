package de.adorsys.openbankinggateway;

import de.adorsys.openbankinggateway.sandbox.SandboxAppsStarter;
import de.adorsys.openbankinggateway.sandbox.internal.SandboxApp;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class BasicTest extends WithSandboxSpringBootTest {

    private final SandboxAppsStarter executor = new SandboxAppsStarter();

    @Test
    @SneakyThrows
    void testEnvStartsUp() {
        executor.runAll();

        Thread.sleep(20000);

        assertThat(SandboxApp.values()).extracting(it -> it.getLoader().get()).isNotNull();
    }
}
