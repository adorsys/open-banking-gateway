package de.adorsys.opba.protocol.services.psuconsentsession;

import de.adorsys.opba.protocol.services.TppBankingService;

import java.util.Optional;

public interface PsuConsentSessionLoadingService {
    Optional<PsuConsentSession> loadPsuConsentSessionById(String psuConsentSession);

    PsuConsentSession establishNewPsuConsentSession(String bankId, String fintechUserId, String fintechRedirectURLOK, String fintechRedirectURLNOK, TppBankingService desiredService);
}
