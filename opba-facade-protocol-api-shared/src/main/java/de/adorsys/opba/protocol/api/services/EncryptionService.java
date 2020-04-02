package de.adorsys.opba.protocol.api.services;

public interface EncryptionService {

    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);
}
