package de.adorsys.opba.api.security.service;

import de.adorsys.opba.api.security.domain.DataToSign;

public interface RequestSigningService {
    String signature(DataToSign dataToSign);
}
