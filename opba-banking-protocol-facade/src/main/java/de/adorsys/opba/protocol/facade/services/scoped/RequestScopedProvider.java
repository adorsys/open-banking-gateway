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
import de.adorsys.opba.protocol.api.services.scoped.validation.FieldsToIgnoreLoader;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
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
    private final IgnoreFieldsLoaderFactory ignoreFieldsLoaderFactory;

    public RequestScopedProvider(
            @Qualifier(FACADE_CACHE_BUILDER) CacheBuilder cacheBuilder,
            ConsentAccessFactory accessProvider,
            IgnoreFieldsLoaderFactory ignoreFieldsLoaderFactory) {
        this.memoizedProviders = cacheBuilder.build().asMap();
        this.accessProvider = accessProvider;
        this.ignoreFieldsLoaderFactory = ignoreFieldsLoaderFactory;
    }

    public RequestScoped registerForFintechSession(
            Fintech fintech,
            BankProfile profile,
            ServiceSession session,
            ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider,
            SecretKeyWithIv futureAuthorizationSessionKey,
            Supplier<char[]> fintechPassword
    ) {
        ConsentAccess access = accessProvider.forFintech(fintech, session, fintechPassword);
        EncryptionService authorizationSessionEncService = encryptionService(encryptionServiceProvider, futureAuthorizationSessionKey);
        Long protocolId = session.getProtocol().getId();
        return doRegister(profile, access, authorizationSessionEncService, futureAuthorizationSessionKey, protocolId);
    }

    public RequestScoped registerForPsuSession(
            AuthSession authSession,
            ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider,
            SecretKeyWithIv key
    ) {
        EncryptionService encryptionService = encryptionService(encryptionServiceProvider, key);
        ConsentAccess access = accessProvider.forPsuAndAspsp(
                authSession.getPsu(),
                authSession.getProtocol().getBankProfile().getBank(),
                authSession.getParent()
        );
        Long protocolId = authSession.getParent().getProtocol().getId();
        return doRegister(authSession.getProtocol().getBankProfile(), access, encryptionService, key, protocolId);
    }

    public InternalRequestScoped deregister(RequestScoped requestScoped) {
        return memoizedProviders.remove(requestScoped.getEncryptionKeyId());
    }

    @Override
    public RequestScoped findRegisteredByKeyId(String keyId) {
        return memoizedProviders.get(keyId);
    }

    private EncryptionService encryptionService(ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider, SecretKeyWithIv key) {
        return encryptionServiceProvider.forSecretKey(key);
    }

    @NotNull
    private RequestScoped doRegister(
            BankProfile bankProfile,
            ConsentAccess access,
            EncryptionService encryptionService,
            SecretKeyWithIv key,
            Long protocolId
    ) {
        InternalRequestScoped requestScoped = new InternalRequestScoped(
                encryptionService.getEncryptionKeyId(),
                key,
                bankProfile,
                access,
                encryptionService,
                ignoreFieldsLoaderFactory.createIgnoreFieldsLoader(protocolId)
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
        private final FieldsToIgnoreLoader fieldsToIgnoreLoader;

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

        @Override
        public FieldsToIgnoreLoader fieldsToIgnoreLoader() {
            return fieldsToIgnoreLoader;
        }
    }

    private static class TransientStorageImpl implements TransientStorage {

        @Delegate
        @SuppressWarnings("PMD.UnusedPrivateField") // it is used through Delegate - via TransientStorage interface
        private final AtomicReference<Object> value = new AtomicReference<>();
    }
}
