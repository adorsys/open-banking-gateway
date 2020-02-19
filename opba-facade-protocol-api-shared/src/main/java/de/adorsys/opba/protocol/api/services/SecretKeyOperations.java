package de.adorsys.opba.protocol.api.services;

import de.adorsys.opba.protocol.api.dto.KeyWithParamsDto;

public interface SecretKeyOperations {

    byte[] encrypt(byte[] key);

    byte[] decrypt(byte[] key);

    KeyWithParamsDto generateKey(String password, String algo, byte[] salt, int iterCount);

    KeyWithParamsDto generateKey(String password);
}
