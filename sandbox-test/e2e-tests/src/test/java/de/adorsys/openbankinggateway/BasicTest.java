package de.adorsys.openbankinggateway;

import com.google.common.collect.ImmutableSet;
import de.adorsys.openbankinggateway.sandbox.SandboxAppsStarter;
import de.adorsys.openbankinggateway.sandbox.internal.SandboxApp;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;


class BasicTest extends WithSandboxSpringBootTest {

    private final SandboxAppsStarter executor = new SandboxAppsStarter();

    @Test
    @SneakyThrows
    void testEnvStartsUp() {
        executor.run(ImmutableSet.of(SandboxApp.CONSENT_MGMT));

        Thread.sleep(20000);
    }
}
