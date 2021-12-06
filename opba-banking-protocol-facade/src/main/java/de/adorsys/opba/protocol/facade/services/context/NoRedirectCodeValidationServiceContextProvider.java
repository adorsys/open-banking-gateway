package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.config.auth.FacadeConsentAuthConfig;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.fintech.FintechAuthenticator;
import de.adorsys.opba.protocol.facade.services.scoped.RequestScopedProvider;
import org.springframework.stereotype.Service;

/**
 * Service context provider that ignores redirectCode value, necessary for the cases when consent `deny` was necessary.
 */
@Service(NoRedirectCodeValidationServiceContextProvider.NO_REDIRECT_CODE_VALIDATION)
public class NoRedirectCodeValidationServiceContextProvider extends ServiceContextProviderForFintech {

    public static final String NO_REDIRECT_CODE_VALIDATION = "NO_REDIRECT_CODE_VALIDATION_CONTEXT_PROVIDER";

    public NoRedirectCodeValidationServiceContextProvider(
            AuthorizationSessionRepository authSessions,
            FacadeConsentAuthConfig authConfig,
            FintechAuthenticator authenticator,
            BankProfileJpaRepository profileJpaRepository,
            ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider,
            RequestScopedProvider provider,
            EncryptionKeySerde encryptionKeySerde,
            ServiceSessionRepository serviceSessions
    ) {
        super(authSessions, authConfig, authenticator, profileJpaRepository, consentAuthorizationEncryptionServiceProvider, provider, encryptionKeySerde, serviceSessions);
    }

    protected <T extends FacadeServiceableGetter> void validateRedirectCode(T request, AuthSession session) {
        // NOP
    }
}
