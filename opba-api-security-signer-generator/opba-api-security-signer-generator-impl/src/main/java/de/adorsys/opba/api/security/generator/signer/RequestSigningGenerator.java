package de.adorsys.opba.api.security.generator.signer;

import com.squareup.javapoet.ClassName;
import de.adorsys.opba.api.security.generator.api.GeneratedSigner;
import de.adorsys.opba.api.security.generator.api.Signer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RequestSigningGenerator {

    private final SignerGenerator signerGenerator;

    public RequestSigningGenerator(SignerGenerator signerGenerator) {
        this.signerGenerator = signerGenerator;
    }

    public void generate(TypeElement forClass, GeneratedSigner yamlSpec, Filer filer) {
        Map<String, Map<Signer.HttpMethod, Operation>> requestSpecConfig = new HashMap<>();

        Arrays.stream(yamlSpec.openApiYamlPath()).forEach(yamlLocation -> readAllRequestsDefinitions(getYamlLocation(filer, yamlLocation), requestSpecConfig));

        signerGenerator.generate(ClassName.get(forClass).packageName(), yamlSpec, filer, requestSpecConfig);
    }

    private void readAllRequestsDefinitions(URI yamlLocation, Map<String, Map<Signer.HttpMethod, Operation>> requestSpecConfig) {
        OpenAPI api = new OpenAPIV3Parser().read(yamlLocation.toASCIIString());
        for (Map.Entry<String, PathItem> pathEntry : api.getPaths().entrySet()) {
            String path = pathEntry.getKey();

            registerMethod(path, () -> pathEntry.getValue().getGet(), Signer.HttpMethod.GET, requestSpecConfig);
            registerMethod(path, () -> pathEntry.getValue().getPut(), Signer.HttpMethod.PUT, requestSpecConfig);
            registerMethod(path, () -> pathEntry.getValue().getPost(), Signer.HttpMethod.POST, requestSpecConfig);
            registerMethod(path, () -> pathEntry.getValue().getPatch(), Signer.HttpMethod.PATCH, requestSpecConfig);
            registerMethod(path, () -> pathEntry.getValue().getDelete(), Signer.HttpMethod.DELETE, requestSpecConfig);
        }
    }

    private URI getYamlLocation(Filer filer, String location) {
        try {
            return filer.getResource(StandardLocation.CLASS_PATH, "", location).toUri();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
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
