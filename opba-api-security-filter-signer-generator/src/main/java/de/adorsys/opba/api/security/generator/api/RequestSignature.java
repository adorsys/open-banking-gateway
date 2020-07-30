package de.adorsys.opba.api.security.generator.api;

import java.util.Map;

public interface RequestSignature {

    RequestSignature headers(Map<String, String> headers);
    RequestSignature query(Map<String, String> queryParams);
    RequestSignature body(String body);

    String canonicalStringToSign();
}
