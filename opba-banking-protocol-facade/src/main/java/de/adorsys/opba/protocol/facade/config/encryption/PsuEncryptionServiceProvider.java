package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.dto.PubAndPrivKey;
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

    public EncryptionService forPublicAndPrivateKey(UUID keyId, PubAndPrivKey key) {
        return oper.encryptionService(keyId.toString(), key.getPrivateKey(), key.getPublicKey());
    }

    public KeyPair generateKeyPair() {
        return oper.generatePublicPrivateKey();
    }
}
