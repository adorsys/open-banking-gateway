package de.adorsys.openbankinggateway.sandbox.internal;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.loader.LaunchedURLClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ThreadFactory;

/**
 * This class exists for:
 * 1. To intercept Tomcat call to {@link org.apache.catalina.webresources.TomcatURLStreamHandlerFactory#register()}
 * because URL is shared system class.
 * 2. To provide access to ClassLoaders that are created by Spring - to be able to manipulate child Spring
 * application state.
 *
 * TODO: Most probably it should be removed and all stuff should be done using custom Thread class.
 */
@Slf4j
class SandboxAppsThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        return new ClassLoaderCapturingThread(runnable);
    }

    public static class ClassLoaderCapturingThread extends Thread {

        private final SandboxApp app;

        ClassLoaderCapturingThread(Runnable runnable) {
            super(runnable);
            this.app = computeApp(runnable);
        }

        @Override
        @SneakyThrows
        public void setContextClassLoader(ClassLoader loader) {
            if (!app.getLoader().compareAndSet(null, loader)) {
                if (loader instanceof LaunchedURLClassLoader) {
                    throw new IllegalStateException("ClassLoader for " + app.name() + " already exists");
                }
                // in other case it is tomcat or other nested stuff which should be ignored
            } else {
                // Disable tomcat access to shared VM variable once when started
                disableTomcatWar(loader);
            }

            super.setContextClassLoader(
                    new URLClassLoader(
                            new URL[] { /* Here you can add extra jars */},
                            loader
                    )
            );
        }

        @SneakyThrows
        private SandboxApp computeApp(Runnable runnable) {
            // One thread - one task assumption
            Field f = runnable.getClass().getDeclaredField("firstTask");
            f.setAccessible(true);
            return ((SandboxAppExecutor.TaggedFuture) f.get(runnable)).getApp();
        }

        private void disableTomcatWar(ClassLoader loader) {
            try {
                Class<?> cls = loader.loadClass("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory");
                Method disable = cls.getDeclaredMethod("disable");
                disable.invoke(null);

            } catch (NoSuchMethodException | ClassNotFoundException ex) {
                // NOP
            } catch (IllegalAccessException | InvocationTargetException ex) {
                log.error("Failed disabling tomcat", ex);
            }
        }

    }
}
