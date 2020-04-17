package de.adorsys.opba.protocol.facade.config.encryption;

import com.google.common.hash.Hashing;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsentAuthorizationEncryptionServiceProvider {
    private final EncryptionWithInitVectorOper oper;

    public EncryptionService forSecretKey(SecretKeyWithIv key) {
        String keyId = Hashing.sha256().hashBytes(key.getSecretKey().getEncoded()).toString();
        return oper.encryptionService(keyId, key);
    }

    public SecretKeyWithIv generateKey() {
        return oper.generateKey();
    }
}
