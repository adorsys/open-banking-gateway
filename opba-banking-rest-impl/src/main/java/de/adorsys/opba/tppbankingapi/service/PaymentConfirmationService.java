package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Payment;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentConfirmationService {

    private final AuthorizationSessionRepository authSessions;
    private final PaymentRepository paymentRepository;
    private final FintechSecureStorage vault;

    @Transactional
    public boolean confirmPayment(UUID authorizationSessionId, String finTechPassword) {
        Optional<AuthSession> session = authSessions.findById(authorizationSessionId);
        if (!session.isPresent()) {
            return false;
        }

        Collection<Payment> consent = paymentRepository.findByServiceSessionIdOrderByModifiedAtDesc(session.get().getParent().getId());

        if (consent.isEmpty()) {
            return false;
        }

        paymentRepository.setConfirmed(session.get().getParent().getId());

        if (session.get().isPsuAnonymous()) {
            return true;
        }

        return sendPsuKeyFromInboxToFintech(finTechPassword, session.get());
    }

    private boolean sendPsuKeyFromInboxToFintech(String finTechPassword, AuthSession session) {
        var psuAspspKey = vault.psuAspspKeyFromInbox(
                session,
                finTechPassword::toCharArray
        );

        vault.psuAspspKeyToPrivate(
                session,
                session.getFintechUser().getFintech(),
                psuAspspKey,
                finTechPassword::toCharArray
        );
        return true;
    }
}
