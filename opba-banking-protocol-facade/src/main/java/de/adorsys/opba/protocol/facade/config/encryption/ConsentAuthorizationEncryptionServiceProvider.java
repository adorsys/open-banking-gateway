package de.adorsys.opba.protocol.facade.config.encryption;

import com.google.common.hash.Hashing;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.RequiredArgsConstructor;

/**
 * Secret key based encryption for consent authorization.
 */
@RequiredArgsConstructor
public class ConsentAuthorizationEncryptionServiceProvider {

    private final EncryptionWithInitVectorOper oper;

    /**
     * Create encryption service for a given secret key.
     * @param key Secret key to encrypt/decrypt data with.
     * @return Symmetric encryption service.
     */
    public EncryptionService forSecretKey(SecretKeyWithIv key) {
        String keyId = Hashing.sha256().hashBytes(key.getSecretKey().getEncoded()).toString();
        return oper.encryptionService(keyId, key);
    }

    /**
     * Generates random symmetric key.
     * @return Symmetric key
     */
    public SecretKeyWithIv generateKey() {
        return oper.generateKey();
    }
}
