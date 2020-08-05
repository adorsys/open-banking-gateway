package de.adorsys.opba.api.security.generator.api;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public interface RequestDataToSignGenerator {

    /**
     * Computes canonical (normalized) string representation of the request
     * @param toSign Request that is going to be signed
     * @return String value of the {@code toSign}
     */
    String canonicalString(RequestToSign toSign);

    /**
     * Computes shortened form (SHA-256 hash) of the request canonical string, so that it can be used in headers
     * (i.e. XML payment bodies can be huge, so we can hash request string)
     * @param toSign Request that is going to be signed
     * @return Short hash value of the {@code toSign} ready to be used as the request signature
     *
     * Note: Technically hash strength other than collision resistance is not of much importance here as the value
     * is going to be signed with JWS
     */
    default String canonicalStringToSign(RequestToSign toSign) {
        return Hashing.sha256().hashBytes(canonicalString(toSign).getBytes(StandardCharsets.UTF_8)).toString();
    }
}
