package de.adorsys.opba.api.security.generator.impl;

import de.adorsys.opba.api.security.generator.api.RequestSignature;
import de.adorsys.opba.api.security.generator.api.SignatureConfig;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RequiredArgsConstructor
public class RequestSignatureImpl implements RequestSignature {

    private final String path;
    private final SignatureConfig config;

    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String body;

    @Override
    public RequestSignature headers(Map<String, String> headers) {
        Map<String, String> updatedHeaders = new HashMap<>(this.headers);
        updatedHeaders.putAll(headers);

        return new RequestSignatureImpl(path, config, updatedHeaders, queryParams, body);
    }

    @Override
    public RequestSignature query(Map<String, String> queryParams) {
        Map<String, String> updatedParams = new HashMap<>(this.queryParams);
        updatedParams.putAll(queryParams);

        return new RequestSignatureImpl(path, config, headers, updatedParams, body);
    }

    @Override
    public RequestSignature body(String body) {
        return new RequestSignatureImpl(path, config, headers, queryParams, body);
    }

    @Override
    public String sign() {
        StringBuilder signature = new StringBuilder();

        signature.append(path).append("&");
        ensureRequiredHeadersArePresentAndFilterHeaders(headers)
                .forEach((key, value) -> signature.append(key).append("=").append(value).append("&"));
        ensureQueryParamsArePresentAndFilterHeaders(headers)
                .forEach((key, value) -> signature.append(key).append("=").append(value).append("&"));

        ensurePresentIfNeededAndAddBody(signature);
        return signature.toString();
    }

    private void ensurePresentIfNeededAndAddBody(StringBuilder signature) {
        if (config.isBodyRequired() && null == body) {
            throw new IllegalStateException("Body is required");
        }

        if (null != body) {
            signature.append(body);
        }
    }

    private TreeMap<String, String> ensureRequiredHeadersArePresentAndFilterHeaders(Map<String, String> headers) {
        TreeMap<String, String> result = new TreeMap<>();

        config.getRequiredHeaders().forEach(key -> {
            String value = headers.get(key);
            if (null == value) {
                throw new IllegalStateException("Missing required header: " + key);
            }

            result.put(key, value);
        });

        config.getOptionalHeaders().forEach(key -> {
            String value = headers.get(key);
            if (null != value) {
                result.put(key, value);
            }
        });

        return result;
    }

    private TreeMap<String, String> ensureQueryParamsArePresentAndFilterHeaders(Map<String, String> queryParams) {
        TreeMap<String, String> result = new TreeMap<>();

        config.getRequiredQueryParams().forEach(key -> {
            String value = queryParams.get(key);
            if (null == value) {
                throw new IllegalStateException("Missing required query parameter: " + key);
            }

            result.put(key, value);
        });

        config.getOptionalQueryParams().forEach(key -> {
            String value = queryParams.get(key);
            if (null != value) {
                result.put(key, value);
            }
        });

        return result;
    }
}
