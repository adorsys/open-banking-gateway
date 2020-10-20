package de.adorsys.opba.protocol.xs2a.testsandbox.internal;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Special executor service to deal with intricacies while starting spring-boot microservices within
 * same JVM.
 * <p>
 * TODO: Most probably it should be removed and all stuff should be done using custom Thread class.
 */
public class SandboxAppExecutor extends ThreadPoolExecutor {

    private static final long SECONDS_IN_MINUTE = 60L;

    public SandboxAppExecutor(StarterContext ctx) {
        super(
                SandboxApp.values().length,
                SandboxApp.values().length,
                SECONDS_IN_MINUTE,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new SandboxAppsThreadFactory(ctx)
        );
    }


    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new TaggedFuture<>(runnable, value);
    }

    @Getter
    static class TaggedFuture<T> extends FutureTask<T> {

        private final SandboxApp app;

        TaggedFuture(@NotNull Runnable runnable, T result) {
            super(runnable, result);
            this.app = ((SandboxApp.SandboxRunnable) runnable).getApp();
        }
    }
}
