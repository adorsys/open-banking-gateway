package de.adorsys.opba.api.security.service;

import de.adorsys.opba.api.security.domain.SignData;

public interface RequestVerifyingService {
    boolean verify(String signature, String encodedPublicKey, SignData signData);
}
