package de.adorsys.opba.api.security.generator.api;

import java.util.Map;

public interface PathSignature {

    PathSignature headers(Map<String, String> headers);
    PathSignature query(Map<String, String> queryParams);
    PathSignature body(String body);

    String sign();
}
