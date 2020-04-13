package de.adorsys.opba.protocol.api.services;

public interface EncryptionService {

    String getId();
    byte[] encrypt(byte[] data);
    byte[] decrypt(byte[] data);
}
