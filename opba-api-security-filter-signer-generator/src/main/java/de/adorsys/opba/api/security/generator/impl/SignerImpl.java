package de.adorsys.opba.api.security.generator.impl;

import de.adorsys.opba.api.security.generator.api.RequestDataToSignGenerator;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import de.adorsys.opba.api.security.generator.api.Signer;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class SignerImpl implements Signer {

    private final String basePath;
    private final Map<String, Map<HttpMethod, RequestDataToSignGenerator>> paths;

    @Override
    public Signer withBasePath(String basePath) {
        return new SignerImpl(basePath, paths);
    }

    @Override
    public RequestDataToSignGenerator signerFor(RequestToSign toSign) {
        String computedPath = toSign.getPath().substring(basePath.length());
        if (HttpMethod.GET == toSign.getMethod() && matches("/v1/banking/ais/accounts", computedPath)) {
            return new AisAccountDataToSignGenerator();
        }

        throw new IllegalStateException(
                String.format("Cant create signer for %s %s (full path: %s)", toSign.getMethod(), computedPath, toSign.getPath())
        );
    }

    private boolean matches(String definedPath, String path) {
        String[] yamlSegments = definedPath.split("/", -1);
        String[] pathSegments = path.split("/", -1);

        if (definedPath.length() != pathSegments.length) {
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
