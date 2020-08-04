package de.adorsys.opba.api.security.generator.signer;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import de.adorsys.opba.api.security.generator.api.MatcherUtil;
import de.adorsys.opba.api.security.generator.api.RequestDataToSignGenerator;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import de.adorsys.opba.api.security.generator.api.Signer;
import io.swagger.v3.oas.models.Operation;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOError;
import java.io.IOException;
import java.util.Map;

public class SignerGenerator {
    private static final String CLASS_PURPOSE_COMMENT = "This class performs request signing";

    private final DataToSignGenerator dataToSignGenerator;

    public SignerGenerator(DataToSignGenerator dataToSignGenerator) {
        this.dataToSignGenerator = dataToSignGenerator;
    }

    public void generate(String packageName, Filer filer, Map<String, Map<Signer.HttpMethod, Operation>> requestSpecConfig) {
        ClassName targetClass = ClassName.get(packageName, "RequestSignerImpl");
        TypeSpec.Builder signer = TypeSpec
                .classBuilder(targetClass)
                .addModifiers(Modifier.PUBLIC);

        annotateAsGenerated(signer);
        FieldSpec basePath = addClassField(signer);
        addNoArgsConstructor(signer, basePath);
        addConstructor(signer, basePath);
        implementSignerInterface(signer);
        addWithBasePathMethod(signer, targetClass);
        addSignerForMethod(signer, targetClass, filer, requestSpecConfig);

        JavaFile javaFile = JavaFile
                .builder(packageName, signer.build())
                .indent("    ")
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }

    private void annotateAsGenerated(TypeSpec.Builder signer) {
        signer.addAnnotation(AnnotationSpec
                .builder(Generated.class)
                .addMember("value", CodeBlock.of("$S", SignerGenerator.class.getCanonicalName()))
                .addMember("comments", CodeBlock.of("$S", CLASS_PURPOSE_COMMENT))
                .build()
        );
    }

    private FieldSpec addClassField(TypeSpec.Builder signer) {
        FieldSpec field = FieldSpec.builder(ClassName.get(String.class), "basePath", Modifier.PRIVATE, Modifier.FINAL).build();
        signer.addField(field);
        return field;
    }

    private void addNoArgsConstructor(TypeSpec.Builder signer, FieldSpec basePath) {
        MethodSpec.Builder method = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);


        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("this.$N = $S", basePath, "");

        method.addCode(block.build());
        signer.addMethod(method.build());
    }

    private void addConstructor(TypeSpec.Builder signer, FieldSpec basePath) {
        MethodSpec.Builder method = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        ParameterSpec basePathParam = ParameterSpec.builder(ClassName.get(String.class), "basePath").build();

        method.addParameter(basePathParam);

        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("this.$N = $N", basePath, basePathParam);

        method.addCode(block.build());
        method.addJavadoc("@param basePath Server base path (requested path is relative to it)\n");

        signer.addMethod(method.build());
    }

    private void implementSignerInterface(TypeSpec.Builder signer) {
        signer.addSuperinterface(ClassName.get(Signer.class));
    }

    private void addWithBasePathMethod(TypeSpec.Builder signer, ClassName targetClass) {
        MethodSpec.Builder method = MethodSpec
                .methodBuilder("withBasePath")
                .addAnnotation(ClassName.get(Override.class))
                .returns(ClassName.get(Signer.class))
                .addModifiers(Modifier.PUBLIC);

        ParameterSpec basePath = ParameterSpec.builder(ClassName.get(String.class), "basePath").build();

        method.addParameter(basePath);

        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("return new $T($N)", targetClass, basePath);

        method.addCode(block.build());
        method.addJavadoc("@param basePath Server base path (requested path is relative to it)\n");

        signer.addMethod(method.build());
    }

    private void addSignerForMethod(TypeSpec.Builder signer, ClassName targetClass, Filer filer, Map<String, Map<Signer.HttpMethod, Operation>> requestSpecConfig) {
        MethodSpec.Builder method = MethodSpec
                .methodBuilder("signerFor")
                .addAnnotation(ClassName.get(Override.class))
                .returns(ClassName.get(RequestDataToSignGenerator.class))
                .addModifiers(Modifier.PUBLIC);

        ParameterSpec toSign = ParameterSpec.builder(ClassName.get(RequestToSign.class), "toSign").build();

        method.addParameter(toSign);

        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("String computedPath = $N.getPath().substring(basePath.length())", toSign);

        requestSpecConfig.forEach((path, opSpec) ->
                opSpec.forEach((requestMethod, operation) -> {
                    ClassName className = dataToSignGenerator.generate(targetClass.packageName(), generateOperationIdClassName(operation), operation, filer);
                    returnSignerIfBlock(toSign, block, path, requestMethod, className);
                })
        );

        block.addStatement(
                "throw new IllegalStateException(String.format(\"Can't create signer for %s %s (full path: %s)\", $N.getMethod(), computedPath, $N.getPath()))",
                toSign,
                toSign
        );

        method.addCode(block.build());
        method.addJavadoc("@param toSign The request to sign\n");

        signer.addMethod(method.build());
    }

    private void returnSignerIfBlock(ParameterSpec toSign, CodeBlock.Builder block, String path, Signer.HttpMethod requestMethod, ClassName className) {
        CodeBlock ifBlock = CodeBlock.builder()
                .beginControlFlow(
                        "if ($T.$L == $N.getMethod() && $T.matches($S, computedPath))",
                        Signer.HttpMethod.class,
                        requestMethod,
                        toSign,
                        ClassName.get(MatcherUtil.class),
                        path
                )
                .addStatement("return new $T()", className)
                .endControlFlow()
                .build();

        block.add(ifBlock);
    }

    private String generateOperationIdClassName(Operation operation) {
        StringBuilder result = new StringBuilder();
        String[] operSegments =  operation.getOperationId().split(" ", -1);
        for (String segment : operSegments) {
            result.append(segment.substring(0, 1).toUpperCase()).append(segment.substring(1));
        }
        return result.toString();
    }
}
