package de.adorsys.opba.protocol.facade.config;

import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForAspsp;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech;
import de.adorsys.opba.protocol.facade.services.fintech.FintechAuthenticator;
import de.adorsys.opba.protocol.facade.services.scoped.RequestScopedProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static de.adorsys.opba.api.security.GlobalConst.ENABLED_SECURITY_PROFILE;

@Configuration
@Profile(ENABLED_SECURITY_PROFILE)
public class SecurityConfig {

    @Bean(ServiceContextProviderForAspsp.ASPSP_CONTEXT_PROVIDER)
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

    @Bean(ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER)
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
}
