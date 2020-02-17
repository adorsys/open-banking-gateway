package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FacadeEncryptionServiceFactory {

    public static EncryptionService provideEncryptionService(byte[] key) {
        return new EncryptionServiceImpl(key);
    }
}
