package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentConfirmationService {
    private final ConsentRepository consentRepository;

    public boolean confirmConsent(String authId) {
        Optional<Consent> consent = consentRepository.findByServiceSessionAuthSessionId(UUID.fromString(authId));
        if (consent.isPresent()) {
            consent.get().setConfirmed(true);
            return true;
        }
        return false;
    }
}
