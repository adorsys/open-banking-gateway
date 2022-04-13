package de.adorsys.opba.protocol.facade.services.scoped.consentaccess;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Consent encryption supporting wrapper, to be passed to protocol.
 */
@Getter
@RequiredArgsConstructor
public class ProtocolFacingConsentImpl implements ProtocolFacingConsent {

    private final Consent consent;
    private final EncryptionService encryptionService;
    private final ConsentAuthorizationEncryptionServiceProvider encServiceProvider;
    private final EncryptionKeySerde encryptionKeySerde;

    /**
     * Consent ID that is to be used to communicate with ASPSP.
     */
    @Override
    public String getConsentId() {
        return consent.getConsentId(encryptionService);
    }

    /**
     * Description of the parameters associated with this consent, i.e. list of IBANs that this consent applies to.
     */
    @Override
    public String getConsentContext() {
        return consent.getContext(encryptionService);
    }

    /**
     * Returns cached data (i.e. transaction list) related to the consent.
     */
    @Override
    public String getConsentCache() {
        return consent.getCache(encryptionService);
    }

    /**
     * Set cached data (i.e. cached transaction list) related to the consent.
     */
    @Override
    public void setConsentCache(String cache) {
        consent.setCache(encryptionService, cache);
    }

    /**
     * Set consent ID that is to be used to communicate with ASPSP.
     */
    @Override
    public void setConsentId(String id) {
        consent.setConsentId(encryptionService, id);
    }

    /**
     * Set description of the parameters associated with this consent, i.e. list of IBANs that this consent applies to.
     */
    @Override
    public void setConsentContext(String context) {
        consent.setContext(encryptionService, context);
    }

    @Override
    public EncryptionService getSupplementaryEncryptionService() {
        var encSupplementaryKey = consent.getEncSupplementaryKey(encryptionService);

        if (encSupplementaryKey == null) {
            consent.setEncSupplementaryKey(encryptionService, encryptionKeySerde.asString(encServiceProvider.generateKey()));
        }

        return encServiceProvider.forSecretKey(encryptionKeySerde.fromString(consent.getEncSupplementaryKey(encryptionService)));
    }
}
