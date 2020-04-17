package de.adorsys.opba.protocol.facade.config.encryption;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;

import java.security.KeyPair;

@RequiredArgsConstructor
public abstract class AuthorizationEncryptionServiceProvider {

    private final EncryptionWithInitVectorOper oper;

    public EncryptionServiceWithKey forSecretKey(SecretKeyWithIv key) {
        String keyId = Hashing.sha256().hashBytes(key.getSecretKey().getEncoded()).toString();
        return oper.encryptionService(keyId, key);
    }

    public SecretKeyWithIv generateKey() {
        return oper.generateKey();
    }

    public KeyPair generateKeyPair() {
        return oper.generatePublicPrivateKey();
    }
}
