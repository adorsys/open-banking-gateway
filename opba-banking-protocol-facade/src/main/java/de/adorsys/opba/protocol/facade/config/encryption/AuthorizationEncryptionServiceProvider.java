package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AuthorizationEncryptionServiceProvider {

    private final Map<String, EncryptionService> cachedServices;
    private final EncryptionWithInitVectorOper oper;

    public void remove(EncryptionService service) {
        cachedServices.remove(service.getId());
    }

    public EncryptionService getEncryptionById(String id) {
        return cachedServices.get(id);
    }

    public EncryptionService forSecretKey(SecretKeyWithIv key) {
        EncryptionService service = oper.encryptionService(key);
        cachedServices.put(service.getId(), service);
        return service;
    }

    public SecretKeyWithIv generateKey() {
        return oper.generateKey();
    }
}
