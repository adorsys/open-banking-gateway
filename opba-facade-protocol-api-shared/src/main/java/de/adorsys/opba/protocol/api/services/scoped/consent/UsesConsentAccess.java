package de.adorsys.opba.protocol.api.services.scoped.consent;

/**
 * Protocol facing consent access object. Encapsulates consent operations into a single object.
 */
public interface UsesConsentAccess {

    /**
     * Get consent access for the protocol, so that protocol can create or modify consent without any knowledge
     * of consent persistence or encryption.
     */
    ConsentAccess consentAccess();
}
