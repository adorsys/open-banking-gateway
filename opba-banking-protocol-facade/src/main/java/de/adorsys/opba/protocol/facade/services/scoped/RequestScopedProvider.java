package de.adorsys.opba.protocol.facade.services.scoped;

import com.google.common.cache.CacheBuilder;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
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
import java.util.function.Supplier;

import static de.adorsys.opba.protocol.facade.config.FacadeTransientDataConfig.FACADE_CACHE_BUILDER;

@Service
public class RequestScopedProvider implements RequestScopedServicesProvider {

    private final Map<String, InternalRequestScoped> memoizedProviders;
    private final ConsentAccessFactory accessProvider;

    public RequestScopedProvider(
            @Qualifier(FACADE_CACHE_BUILDER) CacheBuilder cacheBuilder,
            ConsentAccessFactory accessProvider
    ) {
        this.memoizedProviders = cacheBuilder.build().asMap();
        this.accessProvider = accessProvider;
    }

    public RequestScoped registerForFintechSession(
            Fintech fintech,
            BankProfile profile,
            ServiceSession session,
            AuthorizationEncryptionServiceProvider encryptionServiceProvider,
            SecretKeyWithIv futureAuthorizationSessionKey,
            Supplier<char[]> fintechPassword
    ) {
        ConsentAccess access = accessProvider.forFintech(fintech, session, fintechPassword);
        EncryptionServiceWithKey encryptionService = encryptionService(encryptionServiceProvider, futureAuthorizationSessionKey);
        return doRegister(profile, access, encryptionService, futureAuthorizationSessionKey);
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
                encryptionService // TODO this should be PSU public key based
        );

        return doRegister(session.getProtocol().getBankProfile(), access, encryptionService, key);
    }

    public InternalRequestScoped deregister(RequestScoped requestScoped) {
        return memoizedProviders.remove(requestScoped.getEncryptionKeyId());
    }

    @Override
    public RequestScoped findRegisteredByKeyId(String keyId) {
        return memoizedProviders.get(keyId);
    }

    private EncryptionServiceWithKey encryptionService(AuthorizationEncryptionServiceProvider encryptionServiceProvider, SecretKeyWithIv key) {
        return encryptionServiceProvider.forSecretKey(key);
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

        private final TransientStorage transientStorage = new TransientStorageImpl();

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
            return transientStorage;
        }
    }

    private static class TransientStorageImpl implements TransientStorage {

        @Delegate
        @SuppressWarnings("PMD.UnusedPrivateField") // it is used through Delegate - via TransientStorage interface
        private final AtomicReference<Object> value = new AtomicReference<>();
    }
}
