package de.adorsys.opba.protocol.facade.utils;

import static de.adorsys.opba.protocol.facade.config.EncryptionConfig.SALT_LENGTH;
import static de.adorsys.opba.protocol.facade.config.EncryptionConfig.SECURE_RANDOM;

public class EncryptionUtils {

    public static byte[] getNewSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
}
