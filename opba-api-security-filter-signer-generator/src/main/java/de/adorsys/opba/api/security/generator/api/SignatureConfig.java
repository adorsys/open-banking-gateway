package de.adorsys.opba.api.security.generator.api;

import lombok.Value;

import java.util.Set;

@Value
public class SignatureConfig {

    Set<String> requiredHeaders;
    Set<String> optionalHeaders;
    Set<String> requiredQueryParams;
    Set<String> optionalQueryParams;
    boolean bodyRequired;
}
