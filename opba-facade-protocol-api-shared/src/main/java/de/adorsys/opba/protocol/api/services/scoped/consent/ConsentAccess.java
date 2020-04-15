package de.adorsys.opba.protocol.api.services.scoped.consent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsentAccess {

    ProtocolFacingConsent createDoNotPersist(UUID internalId);
    void save(ProtocolFacingConsent consent);
    void delete(ProtocolFacingConsent consent);
    Optional<ProtocolFacingConsent> findByInternalId(UUID internalId);
    List<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu();
}
