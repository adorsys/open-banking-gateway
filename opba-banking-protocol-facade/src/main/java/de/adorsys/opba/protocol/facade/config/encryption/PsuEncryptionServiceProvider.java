package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.dto.PubAndPrivKey;
import lombok.RequiredArgsConstructor;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

/**
 * PSU/Fintech user encryption provider.
 */
@RequiredArgsConstructor
public class PsuEncryptionServiceProvider {

    private final CmsEncryptionOper oper;

    /**
     * Public key (write only) encryption.
     * @param keyId Key ID
     * @param key Public key
     * @return Encryption service for writing only
     */
    public EncryptionService forPublicKey(UUID keyId, PublicKey key) {
        return oper.encryptionService(keyId.toString(), key);
    }

    /**
     * Private key (read only) encryption.
     * @param keyId Key ID
     * @param key Private key
     * @return Encryption service for reading only
     */
    public EncryptionService forPrivateKey(UUID keyId, PrivateKey key) {
        return oper.encryptionService(keyId.toString(), key);
    }

    /**
     * Public and Private key (read/write) encryption.
     * @param keyId Key ID
     * @param key Public-Private key pair
     * @return Encryption service for both reading and writing
     */
    public EncryptionService forPublicAndPrivateKey(UUID keyId, PubAndPrivKey key) {
        return oper.encryptionService(keyId.toString(), key.getPrivateKey(), key.getPublicKey());
    }

    /**
     * Generate random key pair.
     * @return Random key pair.
     */
    public KeyPair generateKeyPair() {
        return oper.generatePublicPrivateKey();
    }
}
