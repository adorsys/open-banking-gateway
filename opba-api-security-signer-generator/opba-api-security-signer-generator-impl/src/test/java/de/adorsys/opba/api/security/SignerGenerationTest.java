package de.adorsys.opba.api.security;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import de.adorsys.opba.api.security.generator.SignerGeneratingProcessor;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class SignerGenerationTest {

    @Test
    void testBasicCases() throws IOException {
        Compilation compilation = javac()
                .withProcessors(new SignerGeneratingProcessor())
                .compile(JavaFileObjects.forResource("SignerConfigurer.java"));

        assertThat(compilation).succeededWithoutWarnings();

        for (JavaFileObject generated : compilation.generatedSourceFiles()) {
            System.out.println(generated.getCharContent(true));
        }
    }
}
