package de.adorsys.openbankinggateway;

import de.adorsys.openbankinggateway.sandbox.SandboxAppsStarter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;


class BasicTest extends WithSandboxSpringBootTest {

    private final SandboxAppsStarter executor = new SandboxAppsStarter();

    @Test
    @SneakyThrows
    void testEnvStartsUp() {
        executor.runAll();

        Thread.sleep(20000);
    }
}
