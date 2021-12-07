package de.adorsys.opba.protocol.facade.config.encryption;

import lombok.RequiredArgsConstructor;

import java.security.KeyPair;

/**
 * Fintech-only facing encryption service.
 */
@RequiredArgsConstructor
public class FintechOnlyEncryptionServiceProvider {

    private final CmsEncryptionOper oper;

    public KeyPair generateKeyPair() {
        return oper.generatePublicPrivateKey();
    }
}
