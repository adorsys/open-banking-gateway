package de.adorsys.opba.protocol.api.services;

public interface EncryptionService {

    String getEncryptionKeyId();
    byte[] encrypt(byte[] data);
    byte[] decrypt(byte[] data);
}
