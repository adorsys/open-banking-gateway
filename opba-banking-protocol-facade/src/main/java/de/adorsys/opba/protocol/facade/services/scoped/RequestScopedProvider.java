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
import de.adorsys.opba.protocol.facade.services.scoped.consentaccess.ConsentAccessFactory;
import de.adorsys.opba.protocol.facade.services.scoped.paymentaccess.PaymentAccessFactory;
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

/**
 * Provides request scoped services (i.e. encryption and consent access) to the protocols.
 */
@Service
public class RequestScopedProvider implements RequestScopedServicesProvider {

    private final Map<String, InternalRequestScoped> memoizedProviders;
    private final ConsentAccessFactory consentAccessProvider;
    private final PaymentAccessFactory paymentAccessProvider;
    private final IgnoreFieldsLoaderFactory ignoreFieldsLoaderFactory;
    private final ApiConsumerConfig fintechConfig;

    public RequestScopedProvider(
            @Qualifier(FACADE_CACHE_BUILDER) CacheBuilder cacheBuilder,
            ConsentAccessFactory consentAccessProvider,
            PaymentAccessFactory paymentAccessProvider,
            IgnoreFieldsLoaderFactory ignoreFieldsLoaderFactory,
            ApiConsumerConfig fintechConfig) {
        this.memoizedProviders = cacheBuilder.build().asMap();
        this.consentAccessProvider = consentAccessProvider;
        this.paymentAccessProvider = paymentAccessProvider;
        this.ignoreFieldsLoaderFactory = ignoreFieldsLoaderFactory;
        this.fintechConfig = fintechConfig;
    }

    /**
     * Registers scoped services for the FinTech request.
     * @param fintech FinTech to provide services for.
     * @param profile ASPSP profile (i.e. FinTS or Xs2a)
     * @param session Owning session for current scoped services
     * @param bankProtocolId Bank protocol id to scope the services more precisely
     * @param encryptionServiceProvider Consent encryption services for the FinTech
     * @param futureAuthorizationSessionKey Authorization session key that is going to be used (if the session is not opened yet)
     *                                      or current session key if it is already opened
     * @param fintechPassword Fintech Datasafe/KeyStore access password
     * @return Request scoped services for FinTech
     */
    public RequestScoped registerForFintechSession(
            Fintech fintech,
            BankProfile profile,
            ServiceSession session,
            Long bankProtocolId,
            ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider,
            SecretKeyWithIv futureAuthorizationSessionKey,
            Supplier<char[]> fintechPassword
    ) {
        ConsentAccess consentAccess = consentAccessProvider.consentForFintech(fintech, profile.getBank(), session, fintechPassword);
        PaymentAccess paymentAccess = paymentAccessProvider.paymentForFintech(fintech, session, fintechPassword);

        EncryptionService authorizationSessionEncService = sessionEncryption(encryptionServiceProvider, futureAuthorizationSessionKey);
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
            Long bankProtocolId,
            SecretKeyWithIv key
    ) {
        EncryptionService sessionEncryption = sessionEncryption(encryptionServiceProvider, key);

        ConsentAccess consentAccess = getPsuConsentAccess(authSession);
        PaymentAccess paymentAccess = getPsuPaymentAccess(authSession);

        return doRegister(
                authSession.getAction().getBankProfile(),
                fintechConfig.getConsumers().get(authSession.getFintechUser().getFintech().getGlobalId()),
                consentAccess,
                paymentAccess,
                sessionEncryption,
                key,
                bankProtocolId);
    }

    private ConsentAccess getPsuConsentAccess(AuthSession authSession) {
        if (authSession.isPsuAnonymous()) {
            if (null != authSession.getPsu()) {
                throw new IllegalStateException("Expected anonymous session");
            }

            return consentAccessProvider.consentForAnonymousPsu(
                    authSession.getFintechUser().getFintech(),
                    authSession.getAction().getBankProfile().getBank(),
                    authSession.getParent()
            );
        }

        return consentAccessProvider.consentForPsuAndAspsp(
                authSession.getPsu(),
                authSession.getAction().getBankProfile().getBank(),
                authSession.getParent());
    }

    private PaymentAccess getPsuPaymentAccess(AuthSession authSession) {
        if (authSession.isPsuAnonymous()) {
            if (null != authSession.getPsu()) {
                throw new IllegalStateException("Expected anonymous session");
            }

            return paymentAccessProvider.paymentForAnonymousPsu(
                    authSession.getFintechUser().getFintech(),
                    authSession.getAction().getBankProfile().getBank(),
                    authSession.getParent()
            );
        }

        return paymentAccessProvider.paymentForPsuAndAspsp(
                authSession.getPsu(),
                authSession.getAction().getBankProfile().getBank(),
                authSession.getParent());
    }

    public InternalRequestScoped deregister(RequestScoped requestScoped) {
        return memoizedProviders.remove(requestScoped.getEncryptionKeyId());
    }

    public InternalRequestScoped getInternalRequestScoped(RequestScoped requestScoped) {
        return memoizedProviders.get(requestScoped.getEncryptionKeyId());
    }

    @Override
    public RequestScoped findRegisteredByKeyId(String keyId) {
        return memoizedProviders.get(keyId);
    }

    private EncryptionService sessionEncryption(ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider, SecretKeyWithIv key) {
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
