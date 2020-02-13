package de.adorsys.opba.protocol.api.services;

import javax.crypto.SecretKey;

public interface EncryptionService {

    byte[] encryptSecretKey(byte[] key);

    byte[] decryptSecretKey(byte[] encryptedKey);

    byte[] encrypt(byte[] data, byte[] key);

    byte[] decrypt(byte[] encrypted, byte[] key);

    SecretKey generateKey(String password, String algo, byte[] salt, int iterCount);

    SecretKey generateKey(String password, byte[] salt);

    SecretKey generateKey(String password);
}
