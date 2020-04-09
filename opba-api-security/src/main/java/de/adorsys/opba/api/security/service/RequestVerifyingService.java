package de.adorsys.opba.api.security.service;

import de.adorsys.opba.api.security.domain.DataToSign;

public interface RequestVerifyingService {
    boolean verify(String signature, String encodedPublicKey, DataToSign dataToSign);
}
