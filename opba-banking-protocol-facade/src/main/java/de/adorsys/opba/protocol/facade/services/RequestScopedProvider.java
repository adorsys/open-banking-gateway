package de.adorsys.opba.protocol.facade.services;

import com.google.common.cache.CacheBuilder;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.protocol.facade.config.FacadeTransientDataConfig.FACADE_CACHE_BUILDER;

@Service
public class RequestScopedProvider implements RequestScopedServicesProvider {

    private final Map<String, InternalRequestScoped> cachedProviders;

    public RequestScopedProvider(@Qualifier(FACADE_CACHE_BUILDER) CacheBuilder cacheBuilder) {
        this.cachedProviders = cacheBuilder.build().asMap();
    }

    public RequestScoped register(SecretKeyWithIv key) {
        InternalRequestScoped requestScoped = new InternalRequestScoped(key);
        return cachedProviders.put(requestScoped.getEncryptionKeyId(), requestScoped);
    }

    public SecretKeyWithIv keyFor(RequestScoped requestScoped) {
        return cachedProviders.get(requestScoped.getEncryptionKeyId());
    }

    public void deRegister(RequestScoped requestScoped) {
        cachedProviders.remove(requestScoped.getEncryptionKeyId());
    }

    @Override
    public RequestScoped byEncryptionKeyId(String keyId) {
        return cachedProviders.get(keyId);
    }

    @Getter
    @RequiredArgsConstructor
    public static class InternalRequestScoped implements RequestScoped {

        private final SecretKeyWithIv key;


    }
}
