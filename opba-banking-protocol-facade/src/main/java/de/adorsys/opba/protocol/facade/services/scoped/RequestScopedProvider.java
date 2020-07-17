package de.adorsys.opba.protocol.facade.services.scoped;

import com.google.common.cache.CacheBuilder;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.api.common.CurrentFintechProfile;
import de.adorsys.opba.protocol.api.fintechspec.ApiConsumerConfig;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
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
    private final ApiConsumerConfig fintechConfig;

    public RequestScopedProvider(
            @Qualifier(FACADE_CACHE_BUILDER) CacheBuilder cacheBuilder,
            ConsentAccessFactory accessProvider,
            IgnoreFieldsLoaderFactory ignoreFieldsLoaderFactory,
            ApiConsumerConfig fintechConfig) {
        this.memoizedProviders = cacheBuilder.build().asMap();
        this.accessProvider = accessProvider;
        this.ignoreFieldsLoaderFactory = ignoreFieldsLoaderFactory;
        this.fintechConfig = fintechConfig;
    }

    public RequestScoped registerForFintechSession(
            Fintech fintech,
            BankProfile profile,
            ServiceSession session,
            long bankProtocolId,
            ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider,
            SecretKeyWithIv futureAuthorizationSessionKey,
            Supplier<char[]> fintechPassword
    ) {
        ConsentAccess consentAccess = accessProvider.consentForFintech(fintech, session, fintechPassword);
        PaymentAccess paymentAccess = accessProvider.paymentForFintech(fintech, session, fintechPassword);

        EncryptionService authorizationSessionEncService = encryptionService(encryptionServiceProvider, futureAuthorizationSessionKey);
        return doRegister(
                profile,
                fintechConfig.getConsumers().get(fintech.getGlobalId()),
                consentAccess,
                paymentAccess,
                authorizationSessionEncService,
                futureAuthorizationSessionKey,
                bankProtocolId);
    }

    public RequestScoped registerForPsuSession(
            AuthSession authSession,
            ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider,
            long bankProtocolId,
            SecretKeyWithIv key
    ) {
        EncryptionService encryptionService = encryptionService(encryptionServiceProvider, key);

        ConsentAccess consentAccess = getPsuConsentAccess(authSession);
        PaymentAccess paymentAccess = getPsuPaymentAccess(authSession, encryptionService);

        return doRegister(
                authSession.getAction().getBankProfile(),
                fintechConfig.getConsumers().get(authSession.getFintechUser().getFintech().getGlobalId()),
                consentAccess,
                paymentAccess,
                encryptionService,
                key,
                bankProtocolId);
    }

    private ConsentAccess getPsuConsentAccess(AuthSession authSession) {
        if (authSession.isPsuAnonymous()) {
            return null;
        }

        return accessProvider.consentForPsuAndAspsp(
                authSession.getPsu(),
                authSession.getAction().getBankProfile().getBank(),
                authSession.getParent());
    }

    private PaymentAccess getPsuPaymentAccess(AuthSession authSession, EncryptionService encryptionService) {
        if (authSession.isPsuAnonymous()) {
            return accessProvider.paymentAnonymousPsuAndAspsp(
                    authSession.getAction().getBankProfile().getBank(),
                    encryptionService,
                    authSession.getParent()
            );
        }

        return accessProvider.paymentForPsuAndAspsp(
                authSession.getPsu(),
                authSession.getAction().getBankProfile().getBank(),
                authSession.getParent());
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
            CurrentFintechProfile fintechProfile,
            ConsentAccess consentAccess,
            PaymentAccess paymentAccess,
            EncryptionService encryptionService,
            SecretKeyWithIv key,
            Long protocolId
    ) {
        InternalRequestScoped requestScoped = new InternalRequestScoped(
                encryptionService.getEncryptionKeyId(),
                key,
                bankProfile,
                fintechProfile,
                consentAccess,
                paymentAccess,
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
        private final CurrentFintechProfile fintechProfile;
        private final ConsentAccess consentAccess;
        private final PaymentAccess paymentAccess;
        private final EncryptionService encryptionService;
        private final FieldsToIgnoreLoader fieldsToIgnoreLoader;

        @Override
        public CurrentBankProfile aspspProfile() {
            return bankProfile;
        }

        @Override
        public CurrentFintechProfile fintechProfile() {
            return fintechProfile;
        }

        @Override
        public ConsentAccess consentAccess() {
            return consentAccess;
        }

        @Override
        public PaymentAccess paymentAccess() {
            return paymentAccess;
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
