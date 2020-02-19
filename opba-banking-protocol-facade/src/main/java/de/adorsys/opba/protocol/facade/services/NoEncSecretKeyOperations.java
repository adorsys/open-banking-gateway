package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.dto.NoEncKeyWithParamsDto;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.api.Profiles.NO_ENCRYPTION;

@Service
@Profile(NO_ENCRYPTION)
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
    public NoEncKeyWithParamsDto generateKey(String password, String algo, byte[] salt, int iterCount) {
        return new NoEncKeyWithParamsDto();
    }

    @Override
    public NoEncKeyWithParamsDto generateKey(String password) {
        return new NoEncKeyWithParamsDto();
    }
}
