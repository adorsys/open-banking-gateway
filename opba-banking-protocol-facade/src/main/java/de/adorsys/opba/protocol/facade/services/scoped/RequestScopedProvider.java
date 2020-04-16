package de.adorsys.opba.protocol.facade.services.scoped;

import com.google.common.cache.CacheBuilder;
import com.google.common.hash.Hashing;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.transientdata.TransientStorage;
import de.adorsys.opba.protocol.facade.config.encryption.AuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.EncryptionServiceWithKey;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static de.adorsys.opba.protocol.facade.config.FacadeTransientDataConfig.FACADE_CACHE_BUILDER;

@Service
public class RequestScopedProvider implements RequestScopedServicesProvider {

    private final Map<String, InternalRequestScoped> memoizedProviders;
    private final BankProfileJpaRepository profileJpaRepository;
    private final ConsentAccessFactory accessProvider;

    public RequestScopedProvider(
            @Qualifier(FACADE_CACHE_BUILDER) CacheBuilder cacheBuilder,
            BankProfileJpaRepository profileJpaRepository,
            ConsentAccessFactory accessProvider
    ) {
        this.memoizedProviders = cacheBuilder.build().asMap();
        this.profileJpaRepository = profileJpaRepository;
        this.accessProvider = accessProvider;
    }

    public RequestScoped registerForFintechSession(
            BankProfile profile,
            AuthorizationEncryptionServiceProvider encryptionServiceProvider,
            SecretKeyWithIv key
    ) {
        EncryptionServiceWithKey encryptionService = encryptionService(encryptionServiceProvider, key);
        ConsentAccess access = accessProvider.noAccess();
        return doRegister(profile, access, encryptionService, key);
    }

    public RequestScoped registerForPsuSession(
            AuthSession session,
            AuthorizationEncryptionServiceProvider encryptionServiceProvider,
            SecretKeyWithIv key
    ) {
        EncryptionServiceWithKey encryptionService = encryptionService(encryptionServiceProvider, key);
        ConsentAccess access = accessProvider.forPsuAndAspsp(
                session.getPsu(),
                session.getProtocol().getBankProfile().getBank(),
                session.getParent(),
                encryptionService
        );

        return doRegister(session.getProtocol().getBankProfile(), access, encryptionService, key);
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

    private EncryptionServiceWithKey encryptionService(AuthorizationEncryptionServiceProvider encryptionServiceProvider, SecretKeyWithIv key) {
        String keyId = Hashing.sha256().hashBytes(key.getSecretKey().getEncoded()).toString();
        return encryptionServiceProvider.forSecretKey(keyId, key);
    }

    @NotNull
    private RequestScoped doRegister(
            BankProfile bank,
            ConsentAccess access,
            EncryptionServiceWithKey encryptionService,
            SecretKeyWithIv key) {
        InternalRequestScoped requestScoped = new InternalRequestScoped(
                encryptionService.getEncryptionKeyId(),
                key,
                bank,
                access,
                encryptionService
        );

        memoizedProviders.put(requestScoped.getEncryptionKeyId(), requestScoped);
        return requestScoped;
    }

    @Getter
    @RequiredArgsConstructor
    public static class InternalRequestScoped implements RequestScoped {

        private final String encryptionKeyId;
        private final SecretKeyWithIv key;
        private final CurrentBankProfile bankProfile;
        private final ConsentAccess access;
        private final EncryptionService encryptionService;

        @Override
        public CurrentBankProfile aspspProfile() {
            return bankProfile;
        }

        @Override
        public ConsentAccess consentAccess() {
            return access;
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
