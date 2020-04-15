package de.adorsys.opba.protocol.facade.services;

public interface RequestVerifyingService {
    String verify(String signature, String encodedPublicKey);
}
