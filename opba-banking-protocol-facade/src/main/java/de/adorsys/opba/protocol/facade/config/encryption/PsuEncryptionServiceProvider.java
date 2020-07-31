package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.RequiredArgsConstructor;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

@RequiredArgsConstructor
public class PsuEncryptionServiceProvider {

    private final CmsEncryptionOper oper;

    public EncryptionService forPublicKey(UUID keyId, PublicKey key) {
        return oper.encryptionService(keyId.toString(), key);
    }

    public EncryptionService forPrivateKey(UUID keyId, PrivateKey key) {
        return oper.encryptionService(keyId.toString(), key);
    }

    public KeyPair generateKeyPair() {
        return oper.generatePublicPrivateKey();
    }
}
