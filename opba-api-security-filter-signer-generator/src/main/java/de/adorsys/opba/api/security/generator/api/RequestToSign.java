package de.adorsys.opba.api.security.generator.api;

import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
public class RequestToSign {
    @NonNull
    Signer.HttpMethod method;

    @NonNull
    String path;

    Map<String, String> headers;
    Map<String, String> queryParams;
    String body;
}
