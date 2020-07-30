package de.adorsys.opba.api.security.generator.api;

public interface Signer {

    Signer withBasePath(String basePath);
    PathSignature signFor(String path);
}
