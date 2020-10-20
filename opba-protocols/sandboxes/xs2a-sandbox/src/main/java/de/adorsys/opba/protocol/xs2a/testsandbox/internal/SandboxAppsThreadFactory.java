package de.adorsys.opba.protocol.xs2a.testsandbox.internal;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

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
 * <p>
 * TODO: Most probably it should be removed and all stuff should be done using custom Thread class.
 */
@Slf4j
@RequiredArgsConstructor
class SandboxAppsThreadFactory implements ThreadFactory {

    private final StarterContext ctx;

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        return new ClassLoaderCapturingThread(runnable, ctx);
    }

    public static class ClassLoaderCapturingThread extends Thread {

        private final SandboxApp app;
        private final StarterContext ctx;

        ClassLoaderCapturingThread(Runnable runnable, StarterContext ctx) {
            super(runnable);
            this.app = computeApp(runnable);
            this.ctx = ctx;
        }

        @Override
        @SneakyThrows
        public void setContextClassLoader(ClassLoader loader) {
            if (null == loader) {
                return;
            }

            ctx.getLoader().put(app, loader);
            // Disable tomcat access to shared VM variable once when started
            disableTomcatWar(loader);

            super.setContextClassLoader(
                    new URLClassLoader(
                            new URL[]{/* Here you can add extra jars */},
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
                log.info("Looks like tomcat is not present", ex);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                log.error("Failed disabling tomcat", ex);
            }
        }

    }
}
