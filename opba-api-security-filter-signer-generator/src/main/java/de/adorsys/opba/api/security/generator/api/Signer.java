package de.adorsys.opba.api.security.generator.api;

public interface Signer {

    Signer withBasePath(String basePath);
    RequestDataToSignGenerator signerFor(RequestToSign toSign);

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
