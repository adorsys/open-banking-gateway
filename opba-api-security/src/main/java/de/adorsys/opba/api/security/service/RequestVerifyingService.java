package de.adorsys.opba.api.security.service;

import de.adorsys.opba.api.security.domain.SignatureClaim;

public interface RequestVerifyingService {
    boolean verify(String signature, String encodedPublicKey, SignatureClaim signatureClaim);
}
