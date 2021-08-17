package de.adorsys.opba.protocol.api.services.scoped.consent;

/**
 * Protocol facing access object for users consent, used in scope of FinTech requests.
 */
public interface FintechConsentAccess extends ConsentAccess {

    @Override
    default boolean isFinTechScope() {
        return true;
    }

    @Override
    default ProtocolFacingConsent createDoNotPersist() {
        throw new IllegalStateException("No PSU present - can't create consent");
    }

    ProtocolFacingConsent createAnonymousConsentNotPersist();
}
