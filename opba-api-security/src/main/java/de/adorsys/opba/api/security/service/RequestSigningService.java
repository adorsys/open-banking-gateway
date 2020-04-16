package de.adorsys.opba.api.security.service;

import de.adorsys.opba.api.security.domain.SignData;

public interface RequestSigningService {
    String sign(SignData signData);
}
