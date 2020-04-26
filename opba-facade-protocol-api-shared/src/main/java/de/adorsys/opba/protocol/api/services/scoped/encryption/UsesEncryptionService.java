package de.adorsys.opba.protocol.api.services.scoped.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;

/**
 * Protocol facing encryption access to persist intermediate data that is necessary to execute the protocol.
 */
public interface UsesEncryptionService {

    /**
     * Encryption service to encrypt and decrypt intermediate data that protocol needs to store. For example, protocol
     * execution context like next step, next redirection URL, etc.
     */
    EncryptionService encryption();
}
