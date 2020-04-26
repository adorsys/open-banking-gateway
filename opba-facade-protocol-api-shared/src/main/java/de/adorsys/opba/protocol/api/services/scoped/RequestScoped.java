package de.adorsys.opba.protocol.api.services.scoped;

import de.adorsys.opba.protocol.api.services.scoped.aspsp.UsesCurrentAspspProfile;
import de.adorsys.opba.protocol.api.services.scoped.consent.UsesConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.encryption.UsesEncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.transientdata.UsesTransientStorage;

/**
 * General services that are required for protocol execution.
 */
// TODO - consider migration to custom spring scope
public interface RequestScoped extends UsesEncryptionService, UsesTransientStorage, UsesConsentAccess, UsesCurrentAspspProfile {

    /**
     * Identifier of the encryption key that is used for current execution. Note, that consent is encrypted
     * using different key.
     */
    String getEncryptionKeyId();
}
