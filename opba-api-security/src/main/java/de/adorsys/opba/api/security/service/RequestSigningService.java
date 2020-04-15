package de.adorsys.opba.api.security.service;

import de.adorsys.opba.api.security.domain.SignatureClaim;

public interface RequestSigningService {
    String sign(SignatureClaim signatureClaim);
}
