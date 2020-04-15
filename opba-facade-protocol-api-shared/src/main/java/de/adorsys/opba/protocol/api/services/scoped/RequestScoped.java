package de.adorsys.opba.protocol.api.services.scoped;

import de.adorsys.opba.protocol.api.services.scoped.aspsp.UsesCurrentAspspProfile;
import de.adorsys.opba.protocol.api.services.scoped.consent.UsesConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.encryption.UsesEncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.transientdata.UsesTransientStorage;

// TODO - consider migration to custom spring scope
public interface RequestScoped extends UsesEncryptionService, UsesTransientStorage, UsesConsentAccess, UsesCurrentAspspProfile {

    String getEncryptionKeyId();
}
