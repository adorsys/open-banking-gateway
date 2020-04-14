package de.adorsys.opba.protocol.facade.config.encryption;

import java.util.Map;

public class PsuConsentEncryptionServiceProvider extends AuthorizationEncryptionServiceProvider {
    public PsuConsentEncryptionServiceProvider(Map<String, EncryptionServiceWithKey> cachedServices, EncryptionWithInitVectorOper oper) {
        super(cachedServices, oper);
    }
}
