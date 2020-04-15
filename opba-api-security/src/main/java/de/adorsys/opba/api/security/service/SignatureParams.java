package de.adorsys.opba.api.security.service;

public enum SignatureParams {
    ALGORITHM_RSA("RSA"),
    CLAIM_NAME("sign-data");

    private String value;

    SignatureParams(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
