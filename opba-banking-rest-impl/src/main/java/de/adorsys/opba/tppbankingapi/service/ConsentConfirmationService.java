package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.api.common.SessionStatus;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentConfirmationService {

    private final AuthorizationSessionRepository authSessions;
    private final ConsentRepository consentRepository;
    private final FintechSecureStorage vault;

    @Transactional
    public boolean confirmConsent(UUID authorizationSessionId, String finTechPassword) {
        Optional<AuthSession> session = authSessions.findById(authorizationSessionId);
        if (!session.isPresent()) {
            return false;
        }

        Collection<Consent> consent = consentRepository.findByServiceSessionIdOrderByModifiedAtDesc(session.get().getParent().getId());

        if (consent.isEmpty()) {
            return false;
        }

        consentRepository.setConfirmed(session.get().getParent().getId());
        session.get().setStatus(SessionStatus.ACTIVATED);
        authSessions.save(session.get());

        // Handling anonymous consent grant flow:
        if (null == session.get().getPsu()) {
            return true;
        }

        var psuAspspKey = vault.psuAspspKeyFromInbox(
                session.get(),
                finTechPassword::toCharArray
        );

        vault.psuAspspKeyToPrivate(
                session.get(),
                session.get().getFintechUser().getFintech(),
                psuAspspKey,
                finTechPassword::toCharArray
        );

        return true;
    }
}
