package de.adorsys.opba.testsandbox.internal;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static de.adorsys.opba.testsandbox.internal.SandboxApp.SandboxRunnable;
import static de.adorsys.opba.testsandbox.internal.SandboxApp.values;

/**
 * Special executor service to deal with intricacies while starting spring-boot microservices within
 * same JVM.
 *
 * TODO: Most probably it should be removed and all stuff should be done using custom Thread class.
 */
public class SandboxAppExecutor extends ThreadPoolExecutor {

    private static final long SECONDS_IN_MINUTE = 60L;

    public SandboxAppExecutor() {
        super(
                values().length,
                values().length,
                SECONDS_IN_MINUTE,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new SandboxAppsThreadFactory()
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
            this.app = ((SandboxRunnable) runnable).getApp();
        }
    }
}
