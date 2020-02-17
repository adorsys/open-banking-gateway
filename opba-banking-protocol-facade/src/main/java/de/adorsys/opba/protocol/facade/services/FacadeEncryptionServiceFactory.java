package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class FacadeEncryptionServiceFactory {

    public static EncryptionService provideEncryptionService(byte[] key) {
        return new EncryptionServiceImpl(key);
    }
}
