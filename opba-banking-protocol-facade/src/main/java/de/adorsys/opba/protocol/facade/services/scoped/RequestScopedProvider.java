package de.adorsys.opba.protocol.facade.services.scoped;

import com.google.common.cache.CacheBuilder;
import com.google.common.hash.Hashing;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.transientdata.TransientStorage;
import de.adorsys.opba.protocol.facade.config.encryption.AuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static de.adorsys.opba.protocol.facade.config.FacadeTransientDataConfig.FACADE_CACHE_BUILDER;

@Service
public class RequestScopedProvider implements RequestScopedServicesProvider {

    private final Map<String, InternalRequestScoped> memoizedProviders;
    private final BankProfileJpaRepository profileJpaRepository;
    private final ConsentAccessProvider accessProvider;

    public RequestScopedProvider(
            @Qualifier(FACADE_CACHE_BUILDER) CacheBuilder cacheBuilder,
            BankProfileJpaRepository profileJpaRepository,
            ConsentAccessProvider accessProvider
    ) {
        this.memoizedProviders = cacheBuilder.build().asMap();
        this.profileJpaRepository = profileJpaRepository;
        this.accessProvider = accessProvider;
    }

    public RequestScoped register(
            FacadeServiceableRequest request,
            AuthorizationEncryptionServiceProvider encryptionService,
            SecretKeyWithIv key
    ) {
        String keyId = Hashing.sha256().hashBytes(key.getSecretKey().getEncoded()).toString();
        InternalRequestScoped requestScoped = new InternalRequestScoped(
                keyId,
                key,
                getBankProfile(request),
                accessProvider,
                encryptionService.forSecretKey(keyId, key)
        );

        memoizedProviders.put(requestScoped.getEncryptionKeyId(), requestScoped);
        return requestScoped;
    }

    public SecretKeyWithIv keyFromRegistered(RequestScoped requestScoped) {
        return memoizedProviders.get(requestScoped.getEncryptionKeyId()).getKey();
    }

    public void deregister(RequestScoped requestScoped) {
        memoizedProviders.remove(requestScoped.getEncryptionKeyId());
    }

    @Override
    public RequestScoped findRegisteredByKeyId(String keyId) {
        return memoizedProviders.get(keyId);
    }

    private BankProfile getBankProfile(FacadeServiceableRequest request) {
        return profileJpaRepository.findByBankUuid(request.getBankId())
                .orElseThrow(() -> new IllegalArgumentException("No bank profile: " + request.getBankId()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class InternalRequestScoped implements RequestScoped {

        private final String encryptionKeyId;
        private final SecretKeyWithIv key;
        private final CurrentBankProfile bankProfile;
        private final ConsentAccessProvider accessProvider;
        private final EncryptionService encryptionService;

        @Override
        public CurrentBankProfile aspspProfile() {
            return bankProfile;
        }

        @Override
        public ConsentAccess consentAccess() {
            return accessProvider;
        }

        @Override
        public EncryptionService encryption() {
            return encryptionService;
        }

        @Override
        public TransientStorage transientStorage() {
            return new TransientStorageImpl();
        }
    }

    private static class TransientStorageImpl implements TransientStorage {

        @Delegate
        private final AtomicReference<Object> value = new AtomicReference<>();
    }
}
