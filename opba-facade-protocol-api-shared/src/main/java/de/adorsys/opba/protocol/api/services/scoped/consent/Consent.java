package de.adorsys.opba.protocol.api.services.scoped.consent;

import java.util.UUID;

public interface Consent {
    UUID getInternalId();

    String getConsentId();
    String getConsentContext();

    String setConsentId(String id);
    String setConsentContext(String context);
}
