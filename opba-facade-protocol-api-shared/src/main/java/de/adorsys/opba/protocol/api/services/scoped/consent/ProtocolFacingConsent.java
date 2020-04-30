package de.adorsys.opba.protocol.api.services.scoped.consent;

/**
 * PSU consent representation view for protocol execution.
 */
public interface ProtocolFacingConsent {

    /**
     * Get consent ID that is used to identify this consent in ASPSP API calls.
     * In short, Consent ID that is returned by the ASPSP.
     */
    String getConsentId();

    /**
     * Get the context of this consent to identify its scope, like the list of IBANs or consent scope, its due date, etc.
     */
    String getConsentContext();

    /**
     * Set consent ID that is used to identify this consent in ASPSP API calls.
     */
    void setConsentId(String id);

    /**
     * Get the context of this consent to identify its scope.
     */
    void setConsentContext(String context);
}
