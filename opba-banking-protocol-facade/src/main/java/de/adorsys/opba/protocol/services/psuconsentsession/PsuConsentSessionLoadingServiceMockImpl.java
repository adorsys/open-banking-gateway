package de.adorsys.opba.protocol.services.psuconsentsession;

import de.adorsys.opba.protocol.services.TppBankingService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PsuConsentSessionLoadingServiceMockImpl implements PsuConsentSessionLoadingService {
    @Override
    public Optional<PsuConsentSession> loadPsuConsentSessionById(String psuConsentSession) {
        return Optional.of(new PsuConsentSession(UUID.randomUUID().toString()));
    }

    @Override
    public PsuConsentSession establishNewPsuConsentSession(String bankId, String fintechUserId, String fintechRedirectURLOK, String fintechRedirectURLNOK, TppBankingService desiredService) {
        return new PsuConsentSession(UUID.randomUUID().toString());
    }
}
