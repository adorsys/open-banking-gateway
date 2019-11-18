package de.adorsys.openbankinggateway;

import org.junit.jupiter.api.Test;

import java.util.jar.Attributes;


class BasicTest extends WithSandboxSpringBootTest {

    @Test
    void testEnvStartsUp() {

        JarFile jarFile = new JarFile(file);
        Manifest manifest = jarFile.getManifest(); // warning: can be null
        Attributes attributes = manifest.getMainAttributes();
        String className = attributes.getValue(Attributes.Name.MAIN_CLASS);
    }
}
