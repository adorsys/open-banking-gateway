package de.adorsys.opba.tppbanking.services.psuconsentsession;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PsuConsentSessionLoadingServiceMockImpl implements PsuConsentSessionLoadingService {
    @Override
    public Optional<PsuConsentSession> loadPsuConsentSessionById(String psuConsentSession) {
        return Optional.of(new PsuConsentSession());
    }
}
