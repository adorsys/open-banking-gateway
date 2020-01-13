package de.sandbox.openbankinggateway.sandbox;

import com.google.common.collect.ImmutableSet;
import de.sandbox.openbankinggateway.sandbox.internal.SandboxApp;
import de.sandbox.openbankinggateway.sandbox.internal.SandboxAppExecutor;
import lombok.SneakyThrows;
import org.awaitility.Durations;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

/**
 * Allows to start all sandbox (they imitate bank / ASPSP) applications.
 */
public class SandboxAppsStarter {

    public static final Set<SandboxApp> ALL = ImmutableSet.copyOf(SandboxApp.values());

    private final ExecutorService executor = new SandboxAppExecutor();
    private final AtomicBoolean started = new AtomicBoolean();

    public void run(Set<SandboxApp> apps) {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Sandbox is already started");
        }

        apps.forEach(it -> executor.submit(it.runnable()));
    }

    public void runAll() {
        run(ALL);
    }

    public void awaitForAllStarted(Duration atMost) {
        await().atMost(atMost).pollDelay(Durations.ONE_SECOND).until(SandboxApp::allReadyToUse);
    }

    public void awaitForAllStarted() {
        awaitForAllStarted(Durations.FIVE_MINUTES);
    }

    @SneakyThrows
    public void shutdown() {
        executor.shutdown();
        executor.awaitTermination(Durations.ONE_MINUTE.getSeconds(), TimeUnit.SECONDS);
    }
}
