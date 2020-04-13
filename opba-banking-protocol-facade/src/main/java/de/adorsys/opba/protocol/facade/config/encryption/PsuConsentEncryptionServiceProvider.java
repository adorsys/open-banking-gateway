package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;

import java.util.Map;

public class PsuConsentEncryptionServiceProvider extends AuthorizationEncryptionServiceProvider {
    public PsuConsentEncryptionServiceProvider(Map<String, EncryptionService> cachedServices, EncryptionWithInitVectorOper oper) {
        super(cachedServices, oper);
    }
}
