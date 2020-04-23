package de.adorsys.opba.protocol.api.services.scoped.consent;

import java.util.Collection;
import java.util.Optional;

public interface ConsentAccess {

    ProtocolFacingConsent createDoNotPersist();
    void save(ProtocolFacingConsent consent);
    void delete(ProtocolFacingConsent consent);
    Optional<ProtocolFacingConsent> findByCurrentServiceSession();
    Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu();
}
