package de.adorsys.opba.protocol.api.services;

public interface EncryptionService {

    String id();

    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);
}
