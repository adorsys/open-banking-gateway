package de.adorsys.opba.api.security.generator.signer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.util.Map;

public class SignerGenerator {

    public void generate(TypeElement forClass, String[] yamlSpec, Filer filer) {
        OpenAPI api = new OpenAPIV3Parser().read(yamlSpec[0]);

        for (Map.Entry<String, PathItem> pathEntry : api.getPaths().entrySet()) {
        }
    }
}
