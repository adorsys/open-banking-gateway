package de.adorsys.openbankinggateway.sandbox.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This class launches spring-fat jar in its own class loader (we assume {@link SandboxApp#runnable()}
 * is called in own thread). This way all services are launched in same JVM and we can easily manipulate
 * their state. Shared context (except system classes!) is not a problem, because it is shaded by Spring - each
 * application has its own class loader and context.
 *
 * Each application will receive spring profile based on its lowercase name. I.e. LEDGERS_GATEWAY assumes to have
 * sandbox/application-test-ledgers-gateway.yml as well as common sandbox/application-test-common.yml
 * property files on resources path.
 */
@Slf4j
@Getter
public enum SandboxApp {

    // TODO - adorsys/xs2a-connector-examples ?
    LEDGERS_GATEWAY("gateway-app-5.4.jar"), // TODO Unneeded?
    ASPSP_PROFILE("aspsp-profile-server-5.4-exec.jar"),
    CONSENT_MGMT("cms-standalone-service-5.4.jar"),
    ONLINE_BANKING("online-banking-app-1.7.jar"),
    TPP_REST("tpp-rest-server-1.7.jar"),
    CERT_GENERATOR("certificate-generator-1.7.jar"),
    LEDGERS_APP("ledgers-app-2.0.jar");

    private final AtomicReference<ClassLoader> loader = new AtomicReference<>();

    private final String jar;
    private final String mainClass;
    private final Set<String> activeProfiles;

    SandboxApp(String jar) {
        this.jar = jar;
        this.mainClass = null;
        this.activeProfiles = defaultProfiles();
    }

    SandboxApp(String jar, String mainClass) {
        this.jar = jar;
        this.mainClass = mainClass;
        this.activeProfiles = defaultProfiles();
    }

    @SneakyThrows
    public Runnable runnable() {
        return new SandboxRunnable(this, this::doRun);
    }

    @SneakyThrows
    Method getMainEntryPoint(ClassloaderWithJar classloaderWithJar) {
        String jarPath = classloaderWithJar.getJarPath();
        URLClassLoader loader = classloaderWithJar.getLoader();

        Class<?> cls = loader.loadClass(getMainClass(jarPath));
        return cls.getDeclaredMethod("main", String[].class);
    }

    @SneakyThrows
    String getMainClass(String jarPath) {
        if (null != mainClass) {
            return mainClass;
        }

        JarFile jarFile = new JarFile(jarPath);
        Manifest manifest = jarFile.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        return attributes.getValue(Attributes.Name.MAIN_CLASS);
    }

    private void doRun() {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(name());
        try {
            ClassloaderWithJar classloaderWithJar = new ClassloaderWithJar(jar);
            getMainEntryPoint(classloaderWithJar).invoke(
                    null,
                    (Object) new String[] {
                            "--spring.profiles.active=" + Joiner.on(",").join(activeProfiles),
                            "--spring.config.location=" + buildSpringConfigLocation()
                    }
            );
        } catch (IllegalAccessException | InvocationTargetException ex) {
            log.error("Failed to invoke main() method for {} of {}", name(), jar, ex);
        } catch (RuntimeException ex) {
            log.error("{} from {} jar has terminated exceptionally", name(), jar, ex);
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    @SneakyThrows
    private String buildSpringConfigLocation() {
        return Joiner.on(",").join(
                "classpath:/",
                // Due to different classloader used by Spring we can't reference these in other way:
                Resources.getResource("sandbox/application-test-common.yml").toURI().toASCIIString(),
                Resources.getResource("sandbox/application-" + testProfileName() + ".yml").toURI().toASCIIString()
        );
    }

    private Set<String> defaultProfiles() {
        return ImmutableSet.of(
                "test-common",
                testProfileName()
        );
    }

    private String testProfileName() {
        return "test-" + name().toLowerCase().replaceAll("_", "-");
    }

    @Data
    private static class ClassloaderWithJar {

        private final String jarPath;
        private final URLClassLoader loader;

        @SneakyThrows
        ClassloaderWithJar(String jar) {
            jarPath = Arrays.stream(System.getProperty("java.class.path").split(System.getProperty("path.separator")))
                    .filter(it -> it.endsWith(jar))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException(
                            "Jar " + jar + " not found on classpath: " + System.getProperty("java.class.path"))
                    );

            loader = new URLClassLoader(
                    // It makes no sense to provide anything else except Spring JAR as it will use its own classloader
                    new URL[] {Paths.get(jarPath).toUri().toURL()},
                    null
            );
        }
    }

    @Data
    static class SandboxRunnable implements Runnable {

        private final SandboxApp app;
        private final Runnable toRun;

        @Override
        public void run() {
            toRun.run();
        }
    }
}
