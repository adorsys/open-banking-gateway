package de.adorsys.opba.api.security.internal.service;

public interface RequestVerifyingService {

    boolean verify(String providedSignature, String encodedPublicKey, String computedSignature);
}
