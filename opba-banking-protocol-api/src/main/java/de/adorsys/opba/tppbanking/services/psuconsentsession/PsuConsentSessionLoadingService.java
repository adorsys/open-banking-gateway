package de.adorsys.opba.tppbanking.services.psuconsentsession;

import java.util.Optional;

public interface PsuConsentSessionLoadingService {
    Optional<PsuConsentSession> loadPsuConsentSessionById(String psuConsentSession);
}
