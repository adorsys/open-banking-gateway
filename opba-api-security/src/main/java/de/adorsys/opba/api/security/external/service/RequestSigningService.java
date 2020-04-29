package de.adorsys.opba.api.security.external.service;

import de.adorsys.opba.api.security.external.domain.DataToSign;

public interface RequestSigningService {

    String signature(DataToSign dataToSign);
}
