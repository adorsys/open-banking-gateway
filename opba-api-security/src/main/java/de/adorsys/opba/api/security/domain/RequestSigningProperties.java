package de.adorsys.opba.api.security.domain;

import lombok.Value;

@Value
public class RequestSigningProperties {
    private final String privateKey;
    private final String signIssuer;
    private final String signSubject;
}
