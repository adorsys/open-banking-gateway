package de.adorsys.opba.protocol.api.services;

public interface EncryptionService {

    byte[] encryptPassword(String password);

    byte[] decryptPassword(byte[] encryptedPassword);

    byte[] encrypt(byte[] data, String password);

    byte[] decrypt(byte[] encrypted, String password);
}
