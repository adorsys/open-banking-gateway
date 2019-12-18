package de.adorsys.openbankinggateway.sandbox;

import com.google.common.collect.ImmutableSet;
import de.adorsys.openbankinggateway.sandbox.internal.SandboxApp;
import de.adorsys.openbankinggateway.sandbox.internal.SandboxAppExecutor;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

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
}
