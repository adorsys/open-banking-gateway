package de.adorsys.opba.api.security.generator.api;

import java.util.HashMap;
import java.util.Map;

// Can't safely use Lombok in self-generated classes
public class RequestToSign {

    private final Signer.HttpMethod method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String body;

    private RequestToSign(Signer.HttpMethod method, String path, Map<String, String> headers, Map<String, String> queryParams, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    public RequestToSignBuilder builder() {
        return new RequestToSignBuilder();
    }

    public Signer.HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getBody() {
        return body;
    }

    public static class RequestToSignBuilder {
        private Signer.HttpMethod method;
        private String path;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> queryParams = new HashMap<>();
        private String body;

        public RequestToSignBuilder method(Signer.HttpMethod method) {
            this.method = method;
            return this;
        }

        public RequestToSignBuilder path(String path) {
            this.path = path;
            return this;
        }

        public RequestToSignBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestToSignBuilder queryParams(Map<String, String> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public RequestToSignBuilder body(String body) {
            this.body = body;
            return this;
        }

        public RequestToSign build() {
            if (null == path) {
                throw new IllegalArgumentException("Path can't be null");
            }

            if (null == method) {
                throw new IllegalArgumentException("Method can't be null");
            }


            return new RequestToSign(method, path, headers, queryParams, body);
        }
    }
}
