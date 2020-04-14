package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.ProtocolFacingEncryptionServiceProvider;

import java.util.Map;

public class ConsentAuthorizationEncryptionServiceProvider extends AuthorizationEncryptionServiceProvider implements ProtocolFacingEncryptionServiceProvider {
    public ConsentAuthorizationEncryptionServiceProvider(Map<String, EncryptionServiceWithKey> cachedServices, EncryptionWithInitVectorOper oper) {
        super(cachedServices, oper);
    }
}
