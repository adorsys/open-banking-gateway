package de.adorsys.opba.api.security.generator.api;

public interface Signer {

    Signer withBasePath(String basePath);
    RequestSignature signFor(HttpMethod method, String path);

    enum HttpMethod {
        POST,
        GET,
        PUT,
        PATCH,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE;
    }
}
