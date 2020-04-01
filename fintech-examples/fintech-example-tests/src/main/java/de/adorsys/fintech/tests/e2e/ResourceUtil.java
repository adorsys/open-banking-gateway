package de.adorsys.fintech.tests.e2e;

import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import static java.nio.charset.StandardCharsets.UTF_8;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ResourceUtil {

    @SneakyThrows
    public static String readResource(String path) {
        return Resources.asCharSource(Resources.getResource(path), UTF_8).read();
    }
}
