package de.adorsys.opba.protocol.api.services;

import javax.crypto.SecretKey;

public interface EncryptionServiceProvider {

    EncryptionService getEncryptionById(String id);
    EncryptionService forSecretKey(SecretKey key);
}
