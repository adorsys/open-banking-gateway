package de.adorsys.opba.api.security.generator.impl;

import de.adorsys.opba.api.security.generator.api.RequestSignature;
import de.adorsys.opba.api.security.generator.api.Signer;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class SignerImpl implements Signer {

    private final String basePath;
    private final Map<String, Map<HttpMethod, RequestSignature>> paths;

    @Override
    public Signer withBasePath(String basePath) {
        return new SignerImpl(basePath, paths);
    }

    @Override
    public RequestSignature signFor(HttpMethod method, String path) {
        String computedPath = path.substring(basePath.length());
        return paths.entrySet().stream()
                .filter(it -> matches(it.getKey(), computedPath))
                .map(it -> it.getValue().get(method))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Can't handle %s: %s with base %s", method, path, basePath)));
    }

    private boolean matches(String definedPaths, String path) {
        String[] yamlSegments = definedPaths.split("/", -1);
        String[] pathSegments = path.split("/", -1);

        if (definedPaths.length() != pathSegments.length) {
            return false;
        }

        for (int segment = 0; segment < yamlSegments.length; ++segment) {
            if (yamlSegments[segment].startsWith("{")) {
                // Any match here
                continue;
            }

            if (!yamlSegments[segment].equals(pathSegments[segment])) {
                return false;
            }
        }

        return true;
    }
}
