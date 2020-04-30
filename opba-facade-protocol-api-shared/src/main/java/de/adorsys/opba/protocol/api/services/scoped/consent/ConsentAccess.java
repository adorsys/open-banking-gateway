package de.adorsys.opba.protocol.api.services.scoped.consent;

import java.util.Collection;
import java.util.Optional;

/**
 * Protocol facing access object for users consent.
 */
public interface ConsentAccess {
    /**
     * Factory method for new consent,
     * @return new consent object that was not persisted and that can be modified.
     */
    ProtocolFacingConsent createDoNotPersist();

    /**
     * Save consent object to database.
     */
    void save(ProtocolFacingConsent consent);

    /**
     * Delete consent object from database.
     */
    void delete(ProtocolFacingConsent consent);

    /**
     * Available consent for current session execution.
     */
    Optional<ProtocolFacingConsent> findByCurrentServiceSession();

    /**
     * Lists all consents that are available for current PSU.
     */
    Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu();
}
