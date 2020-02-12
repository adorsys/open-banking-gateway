package de.adorsys.opba.protocol.api.services;

import javax.crypto.SecretKey;

public interface EncryptionService {

    byte[] encryptSecretKey(SecretKey key);

    byte[] decryptSecretKey(byte[] encryptedKey);

    byte[] encrypt(byte[] data, byte[] key);

    byte[] decrypt(byte[] encrypted, byte[] key);

    SecretKey deriveKey(String password);
}
