package de.adorsys.opba.protocol.api.services.scoped.consent;

public interface ProtocolFacingConsent {

    String getConsentId();
    String getConsentContext();

    void setConsentId(String id);
    void setConsentContext(String context);
}
