package de.adorsys.opba.protocol.facade.services.scoped.consentaccess;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProtocolFacingConsentImpl implements ProtocolFacingConsent {

    private final Consent consent;
    private final EncryptionService encryptionService;

    @Override
    public String getConsentId() {
        return consent.getConsentId(encryptionService);
    }

    @Override
    public String getConsentContext() {
        return consent.getContext(encryptionService);
    }

    @Override
    public void setConsentId(String id) {
        consent.setConsentId(encryptionService, id);
    }

    @Override
    public void setConsentContext(String context) {
        consent.setContext(encryptionService, context);
    }
}
