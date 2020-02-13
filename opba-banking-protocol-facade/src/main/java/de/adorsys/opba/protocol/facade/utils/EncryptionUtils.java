package de.adorsys.opba.protocol.facade.utils;

import lombok.experimental.UtilityClass;

import static de.adorsys.opba.protocol.facade.config.EncryptionConfig.SECURE_RANDOM;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class EncryptionUtils {

    public static byte[] getNewSalt(int saltLength) {
        byte[] salt = new byte[saltLength];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
}
