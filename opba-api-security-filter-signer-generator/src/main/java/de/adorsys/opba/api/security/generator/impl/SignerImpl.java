package de.adorsys.opba.api.security.generator.impl;

/*
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
        String[] definedSegments = definedPath.split("/", -1);
        String[] pathSegments = path.split("/", -1);

        if (definedPath.length() != pathSegments.length) {
            return false;
        }

        for (int segment = 0; segment < definedSegments.length; ++segment) {
            if (definedSegments[segment].startsWith("{")) {
                // Any match here
                continue;
            }

            if (!definedSegments[segment].equals(pathSegments[segment])) {
                return false;
            }
        }

        return true;
    }
}
*/