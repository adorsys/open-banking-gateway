package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserRepository;
import de.adorsys.opba.protocol.api.dto.consent.ConsentResult;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import de.adorsys.opba.protocol.facade.services.authorization.internal.SecretKeyForAuthorizationExchangeService;
import de.adorsys.opba.protocol.facade.services.consent.ConsentSearchService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PsuLoginForAisService {

    private final FintechUserSecureStorage vault;
    private final FintechUserRepository fintechUserRepository;
    private final AuthorizationSessionRepository authRepository;
    private final ConsentSearchService searchService;
    private final SecretKeyForAuthorizationExchangeService keyExchange;

    @Transactional
    public Outcome loginAndAssociateAuthSession(String login, String password, UUID authorizationId, String authorizationPassword) {
        AuthSession session = authRepository.findById(authorizationId)
                .orElseThrow(() -> new IllegalStateException("Missing authorization session: " + authorizationId));

        FintechUserSecureStorage.FinTechUserInboxData data = vault.fromInboxForAuth(
                session,
                authorizationPassword::toCharArray
        );

        SecretKey specEncryptionKey = keyExchange.encryptAndStoreForFuture(login, password, data);
        Optional<ConsentResult> consent = searchService.findConsent(data);
        fintechUserRepository.delete(session.getFintechUser());
        return new Outcome(specEncryptionKey, consent.isPresent());
    }

    @RequiredArgsConstructor
    public static class Outcome implements SecretKey {

        @Delegate
        private final SecretKey key;

        @Getter
        private final boolean consentPresent;
    }
}
