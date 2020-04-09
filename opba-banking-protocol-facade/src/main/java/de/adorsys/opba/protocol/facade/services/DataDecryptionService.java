package de.adorsys.opba.protocol.facade.services;

public interface DataDecryptionService {
    String decrypt(String value, byte[] publicKey);
}
