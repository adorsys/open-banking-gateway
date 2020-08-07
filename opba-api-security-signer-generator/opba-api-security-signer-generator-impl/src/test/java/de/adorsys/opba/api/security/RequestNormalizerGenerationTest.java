package de.adorsys.opba.api.security;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import de.adorsys.opba.api.security.generator.DataToSignGeneratingProcessor;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class RequestNormalizerGenerationTest {

    @Test
    void testBasicCases() {
        Compilation compilation = javac()
                .withProcessors(new DataToSignGeneratingProcessor())
                .compile(JavaFileObjects.forResource("DataToSignConfigurer.java"));

        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation)
                .generatedSourceFile("de.adorsys.signer.test.TestSigner")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("expected/ExpectedDataToSignProvider.java"));

        assertThat(compilation)
                .generatedSourceFile("de.adorsys.signer.test.GetTransactions")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("expected/ExpectedGetTransactions.java"));
    }
}
