package de.adorsys.opba.protocol.api.services.scoped.consent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsentAccess {

    Consent createButDontSave(UUID internalId);
    void save(Consent consent);
    void delete(Consent consent);
    Optional<Consent> findByInternalId(UUID internalId);
    List<Consent> getAvailableConsentsForCurrentPsu();
}
