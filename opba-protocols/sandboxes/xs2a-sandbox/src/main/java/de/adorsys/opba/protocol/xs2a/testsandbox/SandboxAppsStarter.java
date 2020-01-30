package de.adorsys.opba.protocol.xs2a.testsandbox;

import com.google.common.collect.ImmutableSet;
import de.adorsys.opba.protocol.xs2a.testsandbox.internal.SandboxAppExecutor;
import de.adorsys.opba.protocol.xs2a.testsandbox.internal.SandboxApp;
import de.adorsys.opba.protocol.xs2a.testsandbox.internal.StarterContext;
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
public class SandboxAppsStarter implements AutoCloseable {

    public static final Set<SandboxApp> ALL = ImmutableSet.copyOf(SandboxApp.values());

    private final ExecutorService executor;
    private final AtomicBoolean started = new AtomicBoolean();
    private final StarterContext context;

    public SandboxAppsStarter() {
        this.context = new StarterContext();
        this.executor = new SandboxAppExecutor(context);
    }

    public StarterContext run(Set<SandboxApp> apps) {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Sandbox is already started");
        }

        apps.forEach(it -> executor.submit(it.runnable(context)));
        return context;
    }

    public StarterContext runAll() {
        run(ALL);
        return context;
    }

    public void awaitForAllStarted(Duration atMost) {
        await().atMost(atMost).pollDelay(Durations.ONE_SECOND).until(() -> SandboxApp.allReadyToUse(context));
    }

    public void awaitForAllStarted() {
        awaitForAllStarted(Durations.FIVE_MINUTES);
    }

    @Override
    public void close() {
        shutdown();
    }

    @SneakyThrows
    public void shutdown() {
        context.getDockerContainer().forEach((key, container) -> container.stop());
        executor.shutdown();
        executor.awaitTermination(Durations.ONE_MINUTE.getSeconds(), TimeUnit.SECONDS);
    }
}
