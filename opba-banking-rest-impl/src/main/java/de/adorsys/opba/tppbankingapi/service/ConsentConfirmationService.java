package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Optional<Consent> consent = consentRepository.findByPsuAndAspsp(
                session.get().getPsu(),
                session.get().getProtocol().getBankProfile().getBank()
        );

        if (!consent.isPresent()) {
            return false;
        }

        consent.get().setConfirmed(true);
        SecretKeyWithIv psuAspspKey = vault.psuAspspKeyFromInbox(
                session.get(),
                finTechPassword::toCharArray
        );

        vault.psuAspspKeyToPrivate(session.get(), psuAspspKey, consent.get(), finTechPassword::toCharArray);
        return true;
    }
}
