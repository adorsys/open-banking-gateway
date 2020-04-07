package de.adorsys.opba.protocol.xs2a.service.storage;

import de.adorsys.opba.protocol.api.services.EncryptionService;

public interface NeedsEncryptionService {

    void setEncryption(EncryptionService encryption);
}
