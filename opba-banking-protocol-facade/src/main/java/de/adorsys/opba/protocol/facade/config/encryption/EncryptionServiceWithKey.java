package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;

public interface EncryptionServiceWithKey extends EncryptionService {

    SecretKeyWithIv getKey();
}
