package de.adorsys.openbankinggateway.sandbox.internal;

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
 */
public class SandboxAppExecutor extends ThreadPoolExecutor {

    public SandboxAppExecutor() {
        super(
                SandboxApp.values().length,
                SandboxApp.values().length,
                60L,
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
            this.app = ((SandboxApp.SandboxRunnable) runnable).getApp();
        }
    }
}
