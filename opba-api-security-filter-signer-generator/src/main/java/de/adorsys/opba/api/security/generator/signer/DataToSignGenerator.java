package de.adorsys.opba.api.security.generator.signer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import de.adorsys.opba.api.security.generator.api.RequestDataToSignGenerator;
import io.swagger.v3.oas.models.Operation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOError;
import java.io.IOException;

public class DataToSignGenerator {

    public ClassName generate(String packageName, String name, Operation operation, Filer filer) {
        ClassName targetClass = ClassName.get(packageName , name);
        TypeSpec.Builder signer = TypeSpec
                .classBuilder(targetClass)
                .addModifiers(Modifier.PUBLIC);

        implementRequestDataToSignInterface(signer);

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

    private void addCanonicalStringToSign(TypeSpec.Builder signer, ClassName targetClass) {
        MethodSpec.Builder method = MethodSpec
                .methodBuilder("canonicalStringToSign")
                .addAnnotation(ClassName.get(Override.class))
                .returns(ClassName.get(String.class))
                .addModifiers(Modifier.PUBLIC);

        ParameterSpec basePath = ParameterSpec.builder(ClassName.get(String.class), "basePath").build();

        method.addParameter(basePath);

        CodeBlock.Builder block = CodeBlock.builder();
        block.addStatement("return new $T($N)", targetClass, basePath);

        method.addCode(block.build());
        method.addJavadoc("@param basePath Server base path (requested path is relative to it)\n");

        signer.addMethod(method.build());
    }
}
