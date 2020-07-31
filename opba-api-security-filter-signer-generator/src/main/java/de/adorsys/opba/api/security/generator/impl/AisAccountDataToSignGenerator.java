package de.adorsys.opba.api.security.generator.impl;

import de.adorsys.opba.api.security.generator.api.RequestDataToSignGenerator;
import de.adorsys.opba.api.security.generator.api.RequestToSign;

public class AisAccountDataToSignGenerator implements RequestDataToSignGenerator {

    @Override
    public String canonicalStringToSign(RequestToSign toSign) {
        StringBuilder result = new StringBuilder();
        // path
        result.append(toSign.getPath()).append("&");

        // headers
        if (null == toSign.getHeaders().get("X-Request-ID")) {
            throw new IllegalStateException("Missing required header 'X-Request-ID'");
        }
        result.append("X-Request-ID=").append(toSign.getHeaders().get("X-Request-ID")).append("&");

        if (null != toSign.getHeaders().get("X-Authorization-Required")) {
            result.append("X-Authorization-Required=").append(toSign.getHeaders().get("X-Authorization-Required")).append("&");
        }

        // query params
        if (null == toSign.getQueryParams().get("search")) {
            throw new IllegalStateException("Missing required query parameter 'search'");
        }
        result.append("search=").append(toSign.getQueryParams().get("search")).append("&");

        if (null != toSign.getQueryParams().get("redirectCode")) {
            result.append("redirectCode=").append(toSign.getQueryParams().get("redirectCode")).append("&");
        }

        // body
        if (null != toSign.getBody()) {
            result.append("body=").append(toSign.getBody()).append("&");
        }

        return result.toString();
    }
}
