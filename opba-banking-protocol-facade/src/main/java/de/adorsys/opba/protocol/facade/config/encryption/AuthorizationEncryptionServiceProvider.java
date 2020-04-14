package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AuthorizationEncryptionServiceProvider {

    private final Map<String, EncryptionServiceWithKey> cachedServices;
    private final EncryptionWithInitVectorOper oper;

    public void remove(EncryptionService service) {
        if (null == service) {
            return;
        }

        cachedServices.remove(service.getId());
    }

    public EncryptionServiceWithKey getEncryptionById(String id) {
        return cachedServices.get(id);
    }

    public EncryptionServiceWithKey forSecretKey(SecretKeyWithIv key) {
        EncryptionServiceWithKey service = oper.encryptionService(key);
        cachedServices.put(service.getId(), service);
        return service;
    }

    public SecretKeyWithIv generateKey() {
        return oper.generateKey();
    }
}
