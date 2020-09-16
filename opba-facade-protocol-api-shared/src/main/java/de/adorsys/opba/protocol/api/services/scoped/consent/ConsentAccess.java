package de.adorsys.opba.protocol.api.services.scoped.consent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Protocol facing access object for users consent.
 */
public interface ConsentAccess {

    /**
     * @return If consent is being accessed on behalf of FinTech.
     */
    boolean isFinTechScope();

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
     * Available consents for current session execution.
     */
    List<ProtocolFacingConsent> findByCurrentServiceSessionOrderByModifiedDesc();

    /**
     * Available consent for current session execution.
     */
    Optional<ProtocolFacingConsent> findSingleByCurrentServiceSession();

    /**
     * Delete consent for current session execution.
     */
    void deleteByCurrentServiceSession();

    /**
     * Lists all consents that are available for current PSU.
     */
    Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu();

    /**
     * Available consent for current session execution with throwing exception
     */
    default ProtocolFacingConsent getFirstByCurrentSession() {
        List<ProtocolFacingConsent> consents = findByCurrentServiceSessionOrderByModifiedDesc();
        if (consents.isEmpty()) {
            throw new IllegalStateException("Context not found");
        }

        return consents.get(0);
    }
}
