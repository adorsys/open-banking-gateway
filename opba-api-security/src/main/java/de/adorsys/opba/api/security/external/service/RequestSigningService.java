package de.adorsys.opba.api.security.external.service;

public interface RequestSigningService {

    String signature(String dataToSign);
}
