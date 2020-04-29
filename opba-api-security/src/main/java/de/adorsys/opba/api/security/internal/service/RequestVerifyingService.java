package de.adorsys.opba.api.security.internal.service;

import de.adorsys.opba.api.security.external.domain.DataToSign;

public interface RequestVerifyingService {
    boolean verify(String signature, String encodedPublicKey, DataToSign dataToSign);
}
