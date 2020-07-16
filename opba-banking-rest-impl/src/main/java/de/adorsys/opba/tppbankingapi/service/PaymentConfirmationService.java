package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Payment;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentConfirmationService {

    private final AuthorizationSessionRepository authSessions;
    private final PaymentRepository paymentRepository;

    @Transactional
    public boolean confirmPayment(UUID authorizationSessionId) {
        Optional<AuthSession> session = authSessions.findById(authorizationSessionId);
        if (!session.isPresent()) {
            return false;
        }

        Collection<Payment> consent = paymentRepository.findByServiceSessionIdOrderByModifiedAtDesc(session.get().getParent().getId());

        if (consent.isEmpty()) {
            return false;
        }

        paymentRepository.setConfirmed(session.get().getParent().getId());
        return true;
    }
}
