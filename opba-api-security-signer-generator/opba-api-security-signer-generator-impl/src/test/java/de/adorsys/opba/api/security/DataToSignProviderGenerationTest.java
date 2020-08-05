package de.adorsys.opba.api.security;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import de.adorsys.opba.api.security.generator.SignerGeneratingProcessor;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class DataToSignProviderGenerationTest {

    @Test
    void testBasicCases() {
        Compilation compilation = javac()
                .withProcessors(new SignerGeneratingProcessor())
                .compile(JavaFileObjects.forResource("SignerConfigurer.java"));

        assertThat(compilation).succeededWithoutWarnings();
    }
}
