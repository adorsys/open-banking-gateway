package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import jakarta.crypto.Cipher;
import jakarta.crypto.KeyGenerator;
import jakarta.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.function.Supplier;

/**
 * Symmetric-key backed encryption providers.
 */
@RequiredArgsConstructor
public class EncryptionWithInitVectorOper {

    private final SymmetricEncSpec encSpec;

    /**
     * Symmetric Key based encryption.
     * @param keyId Key ID
     * @param keyWithIv Key value
     * @return Encryption service that encrypts data with symmetric key provided
     */
    public EncryptionService encryptionService(String keyId, SecretKeyWithIv keyWithIv) {
        return new SymmetricEncryption(
                keyId,
                () -> encryption(keyWithIv),
                () -> decryption(keyWithIv)
        );
    }

    /**
     * Encryption cipher
     * @param keyWithIv Symmetric key and initialization vector
     * @return Symmetric encryption cipher
     */
    @SneakyThrows
    public Cipher encryption(SecretKeyWithIv keyWithIv) {
        Cipher cipher = Cipher.getInstance(encSpec.getCipherAlgo());
        cipher.init(
                Cipher.ENCRYPT_MODE, keyWithIv.getSecretKey(),
                new IvParameterSpec(keyWithIv.getIv())
        );
        return cipher;
    }

    /**
     * Decryption cipher
     * @param keyWithIv Symmetric key and initialization vector
     * @return Symmetric decryption cipher
     */
    @SneakyThrows
    public Cipher decryption(SecretKeyWithIv keyWithIv) {
        Cipher cipher = Cipher.getInstance(encSpec.getCipherAlgo());
        cipher.init(
                Cipher.DECRYPT_MODE, keyWithIv.getSecretKey(),
                new IvParameterSpec(keyWithIv.getIv())
        );
        return cipher;
    }

    /**
     * Generate random symmetric key with initialization vector (IV)
     * @return Secret key with IV
     */
    @SneakyThrows
    public SecretKeyWithIv generateKey() {
        byte[] iv = new byte[encSpec.getIvSize()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        KeyGenerator keyGen = KeyGenerator.getInstance(encSpec.getKeyAlgo());
        keyGen.init(encSpec.getLen());
        return new SecretKeyWithIv(iv, keyGen.generateKey());
    }

    /**
     * Symmetric encryption/decryption service.
     */
    @RequiredArgsConstructor
    public static class SymmetricEncryption implements EncryptionService {

        @Getter
        private final String encryptionKeyId;

        private final Supplier<Cipher> encryption;
        private final Supplier<Cipher> decryption;

        @Override
        @SneakyThrows
        public byte[] encrypt(byte[] data) {
            return encryption.get().doFinal(data);
        }

        @Override
        @SneakyThrows
        public byte[] decrypt(byte[] data) {
            return decryption.get().doFinal(data);
        }
    }
}
