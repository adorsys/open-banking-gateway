package de.adorsys.openbankinggateway.sandbox.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
 *
 * You can specify database type using System property / Environment variable (in order of precedence) `DB_TYPE`:
 * 1. (DEFAULT) DB_TYPE = H2_LOCAL_SERVER - will connect to in-memory H2 instance
 * 2. DB_TYPE = LOCAL_POSTGRES - will connect to local postgres db
 * (you need to prepare schema and users - see prepare-postgres.sql)
 */
@Slf4j
@Getter
public enum SandboxApp {

    LEDGERS_GATEWAY("gateway-app-5.4.jar"), // adorsys/xs2a-connector-examples
    ASPSP_PROFILE("aspsp-profile-server-5.4-exec.jar"), // adorsys/xs2a-aspsp-profile
    CONSENT_MGMT("cms-standalone-service-5.4.jar"), // adorsys/xs2a-consent-management
    ONLINE_BANKING("online-banking-app-1.7.jar"), // adorsys/xs2a-online-banking
    TPP_REST("tpp-rest-server-1.7.jar"), // adorsys/xs2a-tpp-rest-server
    CERT_GENERATOR("certificate-generator-1.7.jar"), // adorsys/xs2a-certificate-generator
    LEDGERS_APP("ledgers-app-2.0.jar"); // adorsys/ledgers

    public static final String DB_TYPE = "DB_TYPE";
    public static final String H2_LOCAL_SERVER = "h2-local-server";

    private static final AtomicBoolean H2_RUNNING = new AtomicBoolean();
    private static final ObjectMapper YML = new ObjectMapper(new YAMLFactory());

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
    public boolean isReadyToUse() {
        JsonNode tree = YML.readTree(Resources.getResource("sandbox/application-test-common.yml"));
        String pointer = "/common/apps/local/" + name().toLowerCase().replaceAll("_", "") + "/port";
        JsonNode port = tree.at(pointer);

        if (!port.isInt()) {
            throw new IllegalStateException("Port for " + pointer + " should be specified");
        }

        try (Socket ignored = new Socket("localhost", port.asInt())) {
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public static boolean allReadyToUse() {
        return SandboxApp.values().length ==
                Arrays.stream(SandboxApp.values()).map(SandboxApp::isReadyToUse).filter(it -> it).count();
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
            buildSpringConfigLocation();
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
                Resources.getResource("sandbox/application-test-db" + dbProfileAndStartDbIfNeeded() + ".yml").toURI().toASCIIString(),
                Resources.getResource("sandbox/application-test-common.yml").toURI().toASCIIString(),
                Resources.getResource("sandbox/application-" + testProfileName() + ".yml").toURI().toASCIIString()
        );
    }

    private Set<String> defaultProfiles() {
        return ImmutableSet.of(
                "test-common",
                "develop",
                testProfileName()
        );
    }

    private String testProfileName() {
        return "test-" + name().toLowerCase().replaceAll("_", "-");
    }

    private static String dbProfileAndStartDbIfNeeded() {
        String profile = getDbProfile();
        if (H2_LOCAL_SERVER.equals(profile)) {
            startH2Server();
        }

        return "-" + profile;
    }

    private static String getDbProfile() {
        String value = System.getProperty(DB_TYPE, System.getenv(DB_TYPE));

        if (null != value) {
            return value.toLowerCase().replaceAll("_", "-");
        }

        return H2_LOCAL_SERVER;
    }

    private static void startH2Server() {
        if (!H2_RUNNING.compareAndSet(false, true)) {
            return;
        }

        try {
            String port = "33333";
            String webPort = "33334";
            Server h2Server = Server.createTcpServer("-tcpPort", port, "-ifNotExists").start();
            Server.createWebServer("-webPort", webPort).start();
            if (h2Server.isRunning(true)) {
                log.info("H2 server was started and is running on tcp port {}, " +
                                "you can open is web interface using http://localhost:{} use sa/sa and " +
                                "url jdbc:h2:tcp://localhost:{}/~/h2/sandbox-db",
                        port,
                        webPort,
                        port);
                populateH2("jdbc:h2:tcp://localhost:" + port + "/~/h2/sandbox-db");
            } else {
                throw new RuntimeException("Could not start H2 server.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server: ", e);
        }
    }

    @SneakyThrows
    private static void populateH2(String connStr) {
        try (Connection conn = DriverManager.getConnection(connStr, "sa", "sa");
             PreparedStatement ps = conn.prepareStatement(
                     Joiner.on("").join(
                             Resources.readLines(
                                     Resources.getResource("sandbox/prepare-h2.sql"),
                                     StandardCharsets.UTF_8
                             )
                     ))
        ) {
            ps.executeUpdate();
        }
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
