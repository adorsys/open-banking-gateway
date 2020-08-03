package de.adorsys.opba.api.security.generator.signer;

import com.squareup.javapoet.ClassName;
import de.adorsys.opba.api.security.generator.api.Signer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RequestSigningGenerator {

    public void generate(TypeElement forClass, String[] yamlSpec, Filer filer) {
        OpenAPI api = new OpenAPIV3Parser().read(yamlSpec[0]);

        Map<String, Map<Signer.HttpMethod, Operation>> requestSpecConfig = new HashMap<>();

        for (Map.Entry<String, PathItem> pathEntry : api.getPaths().entrySet()) {
            String path = pathEntry.getKey();

            registerMethod(path, () -> pathEntry.getValue().getGet(), Signer.HttpMethod.GET, requestSpecConfig);
            registerMethod(path, () -> pathEntry.getValue().getPut(), Signer.HttpMethod.PUT, requestSpecConfig);
            registerMethod(path, () -> pathEntry.getValue().getPost(), Signer.HttpMethod.POST, requestSpecConfig);
            registerMethod(path, () -> pathEntry.getValue().getPatch(), Signer.HttpMethod.PATCH, requestSpecConfig);
            registerMethod(path, () -> pathEntry.getValue().getDelete(), Signer.HttpMethod.DELETE, requestSpecConfig);
        }

        new SignerGenerator(new DataToSignGenerator()).generate(ClassName.get(forClass).packageName(), filer, requestSpecConfig);
    }

    private void registerMethod(
            String path,
            Supplier<Operation> oper,
            Signer.HttpMethod method,
            Map<String, Map<Signer.HttpMethod, Operation>> result
    ) {
        Operation operation = oper.get();
        if (null != operation) {
            result.computeIfAbsent(
                    path,
                    id -> new EnumMap<>(Signer.HttpMethod.class)
            ).put(
                    method,
                    operation
            );
        }
    }
}
