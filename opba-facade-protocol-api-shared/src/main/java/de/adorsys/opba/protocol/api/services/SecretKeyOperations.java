package de.adorsys.opba.protocol.api.services;

public interface SecretKeyOperations {

    byte[] encrypt(byte[] key);

    byte[] decrypt(byte[] key);

    byte[] generateKey(String password, String algo, byte[] salt, int iterCount);

    byte[] generateKey(String password, byte[] salt);

    byte[] generateKey(String password);
}
