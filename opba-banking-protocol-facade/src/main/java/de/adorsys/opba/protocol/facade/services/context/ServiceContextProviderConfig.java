package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.fintech.FintechAuthenticator;
import de.adorsys.opba.protocol.facade.services.scoped.RequestScopedProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ServiceContextProviderConfig {

    @Bean(NoRedirectCodeValidationServiceContextProvider.NO_REDIRECT_CODE_VALIDATION)
    NoRedirectCodeValidationServiceContextProvider serviceContextProviderNoRedirectCodeValidation(
            AuthorizationSessionRepository authSessions,
            FintechAuthenticator authenticator,
            BankProfileJpaRepository profileJpaRepository,
            ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider,
            RequestScopedProvider provider,
            EncryptionKeySerde encryptionKeySerde,
            ServiceSessionRepository serviceSessions
    ) {
        return new NoRedirectCodeValidationServiceContextProvider(authSessions, authenticator, profileJpaRepository,
                consentAuthorizationEncryptionServiceProvider, provider, encryptionKeySerde, serviceSessions);
    }

    @Bean(ServiceContextProviderForAspsp.ASPSP_CONTEXT_PROVIDER)
    @Profile("!security-bypass")
    ServiceContextProviderForFintech serviceContextProviderForAspsp(
            AuthorizationSessionRepository authSessions,
            FintechAuthenticator authenticator,
            BankProfileJpaRepository profileJpaRepository,
            ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider,
            RequestScopedProvider provider,
            EncryptionKeySerde encryptionKeySerde,
            ServiceSessionRepository serviceSessions
    ) {
        return new ServiceContextProviderForAspsp(authSessions, authenticator, profileJpaRepository,
                consentAuthorizationEncryptionServiceProvider, provider, encryptionKeySerde, serviceSessions);
    }

    @Bean(ServiceContextProviderForAspsp.ASPSP_CONTEXT_PROVIDER)
    @Profile("security-bypass")
    ServiceContextProviderForFintech serviceContextProviderForAspspNoRedirectCodeValidation(
            AuthorizationSessionRepository authSessions,
            FintechAuthenticator authenticator,
            BankProfileJpaRepository profileJpaRepository,
            ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider,
            RequestScopedProvider provider,
            EncryptionKeySerde encryptionKeySerde,
            ServiceSessionRepository serviceSessions
    ) {
        return new NoRedirectCodeValidationServiceContextProvider(authSessions, authenticator, profileJpaRepository,
                consentAuthorizationEncryptionServiceProvider, provider, encryptionKeySerde, serviceSessions);
    }

    @Bean(ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER)
    @Profile("!security-bypass")
    ServiceContextProviderForFintech serviceContextProviderForFintech(
            AuthorizationSessionRepository authSessions,
            FintechAuthenticator authenticator,
            BankProfileJpaRepository profileJpaRepository,
            ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider,
            RequestScopedProvider provider,
            EncryptionKeySerde encryptionKeySerde,
            ServiceSessionRepository serviceSessions
    ) {
        return new ServiceContextProviderForFintech(authSessions, authenticator, profileJpaRepository,
                consentAuthorizationEncryptionServiceProvider, provider, encryptionKeySerde, serviceSessions);
    }

    @Bean(ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER)
    @Profile("security-bypass")
    ServiceContextProviderForFintech serviceContextProviderForFintechNoRedirectCodeValidation(
            AuthorizationSessionRepository authSessions,
            FintechAuthenticator authenticator,
            BankProfileJpaRepository profileJpaRepository,
            ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider,
            RequestScopedProvider provider,
            EncryptionKeySerde encryptionKeySerde,
            ServiceSessionRepository serviceSessions
    ) {
        return new NoRedirectCodeValidationServiceContextProvider(authSessions, authenticator, profileJpaRepository,
                consentAuthorizationEncryptionServiceProvider, provider, encryptionKeySerde, serviceSessions);
    }
}
