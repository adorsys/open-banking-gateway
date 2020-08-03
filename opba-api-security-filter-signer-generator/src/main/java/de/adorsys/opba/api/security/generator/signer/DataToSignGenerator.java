package de.adorsys.opba.api.security.generator.signer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import de.adorsys.opba.api.security.generator.api.RequestDataToSignGenerator;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOError;
import java.io.IOException;
import java.util.Comparator;

public class DataToSignGenerator {

    public ClassName generate(String packageName, String name, Operation operation, Filer filer) {
        ClassName targetClass = ClassName.get(packageName , name);
        TypeSpec.Builder signer = TypeSpec
                .classBuilder(targetClass)
                .addModifiers(Modifier.PUBLIC);

        implementRequestDataToSignInterface(signer);
        addCanonicalStringToSign(signer, operation);

        JavaFile javaFile = JavaFile
                .builder(packageName, signer.build())
                .indent("    ")
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException ex) {
            throw new IOError(ex);
        }

        return targetClass;
    }

    private void implementRequestDataToSignInterface(TypeSpec.Builder signer) {
        signer.addSuperinterface(ClassName.get(RequestDataToSignGenerator.class));
    }

    private void addCanonicalStringToSign(TypeSpec.Builder signer, Operation operation) {
        MethodSpec.Builder method = MethodSpec
                .methodBuilder("canonicalStringToSign")
                .addAnnotation(ClassName.get(Override.class))
                .returns(ClassName.get(String.class))
                .addModifiers(Modifier.PUBLIC);

        ParameterSpec toSign = ParameterSpec.builder(ClassName.get(RequestToSign.class), "toSign").build();
        method.addParameter(toSign);

        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("StringBuilder result = new StringBuilder()");
        block.add(createSignature(toSign, operation));
        block.addStatement("return result.toString()");

        method.addCode(block.build());
        method.addJavadoc("@param toSign Request data to sign\n");

        signer.addMethod(method.build());
    }

    private CodeBlock createSignature(ParameterSpec toSign, Operation operation) {
        CodeBlock.Builder block = CodeBlock.builder();
        appendPath(block, toSign);
        appendHeaders(block, operation, toSign);
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

    private void appendHeaders(CodeBlock.Builder block, Operation operation, ParameterSpec toSign) {
        block.addStatement("// Add headers");
        operation.getParameters().stream()
                .filter(it -> it instanceof HeaderParameter)
                .sorted(Comparator.comparing(Parameter::getName))
                .forEach(header -> {
                    if (header.getRequired()) {
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
                    if (queryParam.getRequired()) {
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
        if (operation.getRequestBody().getRequired()) {
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
        block.addStatement("String $L = $N.getHeaders().get($S)", headerVar, toSign, header.getName());
        block.beginControlFlow("if (null == $L || \"\".equals($L))", headerVar, headerVar);
        block.addStatement("throw new IllegalStateException(\"Missing $L mandatory header\")", header.getName());
        block.endControlFlow();
        block.addStatement("result.append($S).append(\"=\").append($L).append(\"&\")", header.getName(), headerVar);
    }

    private void addOptionalHeader(CodeBlock.Builder block, ParameterSpec toSign, Parameter header) {
        block.addStatement("// Optional header $L", header.getName());
        String headerVar = header.getName().replaceAll("-", "_");
        block.addStatement("String $L = $N.getHeaders().get($S)", headerVar, toSign, header.getName());
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
