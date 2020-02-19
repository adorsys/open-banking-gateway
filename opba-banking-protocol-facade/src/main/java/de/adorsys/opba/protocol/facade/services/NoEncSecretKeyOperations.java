package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.dto.KeyWithParamsDto;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("no-enc")
public class NoEncSecretKeyOperations implements SecretKeyOperations {
    @Override
    public byte[] encrypt(byte[] key) {
        return key;
    }

    @Override
    public byte[] decrypt(byte[] key) {
        return key;
    }

    @Override
    public KeyWithParamsDto generateKey(String password, String algo, byte[] salt, int iterCount) {
        return null;
    }

    @Override
    public KeyWithParamsDto generateKey(String password) {
        return null;
    }
}
