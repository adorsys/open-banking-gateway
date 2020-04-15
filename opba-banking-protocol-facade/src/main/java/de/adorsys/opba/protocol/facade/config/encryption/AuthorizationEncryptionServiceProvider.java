package de.adorsys.opba.protocol.facade.config.encryption;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AuthorizationEncryptionServiceProvider {

    private final EncryptionWithInitVectorOper oper;

    public EncryptionServiceWithKey forSecretKey(String keyId, SecretKeyWithIv key) {
        return oper.encryptionService(keyId, key);
    }

    public SecretKeyWithIv generateKey() {
        return oper.generateKey();
    }
}
