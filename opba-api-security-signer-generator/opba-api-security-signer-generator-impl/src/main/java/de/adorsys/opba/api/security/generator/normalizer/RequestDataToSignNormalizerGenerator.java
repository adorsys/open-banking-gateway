package de.adorsys.opba.api.security.generator.normalizer;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import de.adorsys.opba.api.security.generator.api.GeneratedDataToSignNormalizer;
import de.adorsys.opba.api.security.generator.api.RequestDataToSignNormalizer;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;

import jakarta.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOError;
import java.io.IOException;
import java.util.Comparator;

public class RequestDataToSignNormalizerGenerator {

    private static final String CLASS_PURPOSE_COMMENT = "This class provides request signature canonicalization functionality for a concrete request (convert Request to String to sign)";

    public ClassName generate(String packageName, GeneratedDataToSignNormalizer normalizerConfig, String name, Operation operation, Filer filer) {
        ClassName targetClass = ClassName.get(packageName, name);
        TypeSpec.Builder normalizer = TypeSpec
                .classBuilder(targetClass)
                .addModifiers(Modifier.PUBLIC);
        annotateAsGenerated(normalizer);

        implementRequestDataToSignInterface(normalizer);
        addCanonicalString(normalizer, normalizerConfig, operation);

        JavaFile javaFile = JavaFile
                .builder(packageName, normalizer.build())
                .indent("    ")
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException ex) {
            throw new IOError(ex);
        }

        return targetClass;
    }

    private void annotateAsGenerated(TypeSpec.Builder normalizer) {
        normalizer.addAnnotation(AnnotationSpec
                .builder(Generated.class)
                .addMember("value", CodeBlock.of("$S", DataToSignProviderGenerator.class.getCanonicalName()))
                .addMember("comments", CodeBlock.of("$S", CLASS_PURPOSE_COMMENT))
                .build()
        );
    }

    private void implementRequestDataToSignInterface(TypeSpec.Builder normalizer) {
        normalizer.addSuperinterface(ClassName.get(RequestDataToSignNormalizer.class));
    }

    private void addCanonicalString(TypeSpec.Builder normalizer, GeneratedDataToSignNormalizer normalizerConfig, Operation operation) {
        MethodSpec.Builder method = MethodSpec
                .methodBuilder("canonicalString")
                .addAnnotation(ClassName.get(Override.class))
                .returns(ClassName.get(String.class))
                .addModifiers(Modifier.PUBLIC);

        ParameterSpec toSign = ParameterSpec.builder(ClassName.get(RequestToSign.class), "toSign").build();
        method.addParameter(toSign);

        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("StringBuilder result = new StringBuilder()");
        block.add(createSignature(toSign, normalizerConfig, operation));
        block.addStatement("return result.toString()");

        method.addCode(block.build());
        method.addJavadoc("@param toSign Request data to sign\n");

        normalizer.addMethod(method.build());
    }

    private CodeBlock createSignature(ParameterSpec toSign, GeneratedDataToSignNormalizer normalizerConfig, Operation operation) {
        CodeBlock.Builder block = CodeBlock.builder();
        appendPath(block, toSign);
        appendHeaders(block, normalizerConfig, operation, toSign);
        appendQueryParameters(block, operation, toSign);
        appendBody(block, operation, toSign);
        return block.build();
    }

    private void appendPath(CodeBlock.Builder block, ParameterSpec toSign) {
        block.addStatement("// Add path");
        block.beginControlFlow("if (null == $N.getPath() || \"\".equals($N.getPath()))", toSign, toSign);
        block.addStatement("throw new IllegalStateException(\"Missing path\")");
        block.endControlFlow();
        block.addStatement("result.append($N.getPath()).append(\"&\")", toSign);
        block.addStatement("// Done adding path");
    }

    private void appendHeaders(CodeBlock.Builder block, GeneratedDataToSignNormalizer normalizerConfig, Operation operation, ParameterSpec toSign) {
        block.addStatement("// Add headers");
        operation.getParameters().stream()
                .filter(it -> it instanceof HeaderParameter)
                .sorted(Comparator.comparing(Parameter::getName))
                // Filter out signature header itself
                .filter(it -> !it.getName().toLowerCase().equals(normalizerConfig.signatureHeaderName()))
                .forEach(header -> {
                    if (Boolean.TRUE.equals(header.getRequired())) {
                        addMandatoryHeader(block, toSign, header);
                    } else {
                        addOptionalHeader(block, toSign, header);
                    }
                });
        block.addStatement("// Done adding headers");
    }

    private void appendQueryParameters(CodeBlock.Builder block, Operation operation, ParameterSpec toSign) {
        block.addStatement("// Add query parameters");
        operation.getParameters().stream()
                .filter(it -> it instanceof QueryParameter)
                .sorted(Comparator.comparing(Parameter::getName))
                .forEach(queryParam -> {
                    if (Boolean.TRUE.equals(queryParam.getRequired())) {
                        addMandatoryParameter(block, toSign, queryParam);
                    } else {
                        addOptionalParameter(block, toSign, queryParam);
                    }
                });
        block.addStatement("// Done adding query parameters");
    }

    private void appendBody(CodeBlock.Builder block, Operation operation, ParameterSpec toSign) {
        if (null == operation.getRequestBody()) {
            return;
        }

        block.addStatement("// Add body");
        if (Boolean.TRUE.equals(operation.getRequestBody().getRequired())) {
            block.addStatement("// Mandatory body");
            block.addStatement("String body = $N.getBody()", toSign);
            block.beginControlFlow("if (null == body || \"\".equals(body))");
            block.addStatement("throw new IllegalStateException(\"Missing mandatory body\")");
            block.endControlFlow();
            block.addStatement("result.append(\"body=\").append(body)");
        } else {
            block.addStatement("// Optional body");
            block.addStatement("String body = $N.getBody()", toSign);
            block.beginControlFlow("if (null != body && !\"\".equals(body))");
            block.addStatement("result.append(\"body=\").append(body)");
            block.endControlFlow();
        }
        block.addStatement("// Done adding body");
    }

    private void addMandatoryHeader(CodeBlock.Builder block, ParameterSpec toSign, Parameter header) {
        block.addStatement("// Mandatory header $L", header.getName());
        String headerVar = header.getName().replaceAll("-", "_");
        block.addStatement("String $L = $N.getHeaders().get($S)", headerVar, toSign, header.getName().toLowerCase());
        block.beginControlFlow("if (null == $L || \"\".equals($L))", headerVar, headerVar);
        block.addStatement("throw new IllegalStateException(\"Missing $L mandatory header\")", header.getName());
        block.endControlFlow();
        block.addStatement("result.append($S).append(\"=\").append($L).append(\"&\")", header.getName(), headerVar);
    }

    private void addOptionalHeader(CodeBlock.Builder block, ParameterSpec toSign, Parameter header) {
        block.addStatement("// Optional header $L", header.getName());
        String headerVar = header.getName().replaceAll("-", "_");
        block.addStatement("String $L = $N.getHeaders().get($S)", headerVar, toSign, header.getName().toLowerCase());
        block.beginControlFlow("if (null != $L && !\"\".equals($L))", headerVar, headerVar);
        block.addStatement("result.append($S).append(\"=\").append($L).append(\"&\")", header.getName(), headerVar);
        block.endControlFlow();
    }

    private void addMandatoryParameter(CodeBlock.Builder block, ParameterSpec toSign, Parameter parameter) {
        block.addStatement("// Mandatory parameter $L", parameter.getName());
        String headerVar = parameter.getName().replaceAll("-", "_");
        block.addStatement("String $L = $N.getQueryParams().get($S)", headerVar, toSign, parameter.getName());
        block.beginControlFlow("if (null == $L || \"\".equals($L))", headerVar, headerVar);
        block.addStatement("throw new IllegalStateException(\"Missing $L mandatory query parameter\")", parameter.getName());
        block.endControlFlow();
        block.addStatement("result.append($S).append(\"=\").append($L).append(\"&\")", parameter.getName(), headerVar);
    }

    private void addOptionalParameter(CodeBlock.Builder block, ParameterSpec toSign, Parameter parameter) {
        block.addStatement("// Optional parameter $L", parameter.getName());
        String headerVar = parameter.getName().replaceAll("-", "_");
        block.addStatement("String $L = $N.getQueryParams().get($S)", headerVar, toSign, parameter.getName());
        block.beginControlFlow("if (null != $L && !\"\".equals($L))", headerVar, headerVar);
        block.addStatement("result.append($S).append(\"=\").append($L).append(\"&\")", parameter.getName(), headerVar);
        block.endControlFlow();
    }
}
