package de.adorsys.opba.api.security.generator.signer;

import de.adorsys.opba.api.security.generator.api.RequestSignature;
import de.adorsys.opba.api.security.generator.api.SignatureConfig;
import de.adorsys.opba.api.security.generator.api.Signer;
import de.adorsys.opba.api.security.generator.impl.RequestSignatureImpl;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.parser.OpenAPIV3Parser;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SignatureConfigGenerator {

    public void generate(TypeElement forClass, String[] yamlSpec, Filer filer) {
        OpenAPI api = new OpenAPIV3Parser().read(yamlSpec[0]);

        Map<String, Map<Signer.HttpMethod, RequestSignature>> result = new HashMap<>();

        for (Map.Entry<String, PathItem> pathEntry : api.getPaths().entrySet()) {
            String path = pathEntry.getKey();

            registerMethod(path, () -> pathEntry.getValue().getGet(), Signer.HttpMethod.GET, result);
            registerMethod(path, () -> pathEntry.getValue().getPut(), Signer.HttpMethod.PUT, result);
            registerMethod(path, () -> pathEntry.getValue().getPost(), Signer.HttpMethod.POST, result);
            registerMethod(path, () -> pathEntry.getValue().getPatch(), Signer.HttpMethod.PATCH, result);
            registerMethod(path, () -> pathEntry.getValue().getDelete(), Signer.HttpMethod.DELETE, result);
        }
    }

    private void registerMethod(
            String path,
            Supplier<Operation> oper,
            Signer.HttpMethod method,
            Map<String, Map<Signer.HttpMethod, RequestSignature>> result
    ) {
        Operation operation = oper.get();
        if (null != operation) {
            result.computeIfAbsent(
                    path,
                    id -> new EnumMap<>(Signer.HttpMethod.class)
            ).put(
                    method,
                    new RequestSignatureImpl(path, configForOper(operation))
            );
        }
    }

    private SignatureConfig configForOper(Operation oper) {
        Set<String> requiredHeaders = oper.getParameters().stream()
                .filter(it -> it instanceof HeaderParameter)
                .filter(Parameter::getRequired)
                .map(Parameter::getName)
                .collect(Collectors.toSet());
        Set<String> optionalHeaders = oper.getParameters().stream()
                .filter(it -> it instanceof HeaderParameter)
                .filter(it -> !it.getRequired())
                .map(Parameter::getName)
                .collect(Collectors.toSet());

        Set<String> requiredQueryParams = oper.getParameters().stream()
                .filter(it -> it instanceof QueryParameter)
                .filter(Parameter::getRequired)
                .map(Parameter::getName)
                .collect(Collectors.toSet());
        Set<String> optionalQueryParams = oper.getParameters().stream()
                .filter(it -> it instanceof QueryParameter)
                .filter(it -> !it.getRequired())
                .map(Parameter::getName)
                .collect(Collectors.toSet());

        return new SignatureConfig(
                requiredHeaders,
                optionalHeaders,
                requiredQueryParams,
                optionalQueryParams,
                oper.getRequestBody().getRequired()
        );
    }
}
