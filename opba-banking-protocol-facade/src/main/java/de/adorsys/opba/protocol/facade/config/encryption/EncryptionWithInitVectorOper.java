package de.adorsys.opba.protocol.facade.config.encryption;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class EncryptionWithInitVectorOper {

    private final EncSpec encSpec;

    public EncryptionServiceWithKey encryptionService(SecretKeyWithIv keyWithIv) {
        return new Encryption(
                Hashing.sha256().hashBytes(keyWithIv.getSecretKey().getEncoded()).toString(),
                keyWithIv,
                () -> encryption(keyWithIv),
                () -> decryption(keyWithIv)
        );
    }

    @SneakyThrows
    public Cipher encryption(SecretKeyWithIv keyWithIv) {
        Cipher cipher = Cipher.getInstance(encSpec.getCipherAlgo());
        cipher.init(
                Cipher.ENCRYPT_MODE, keyWithIv.getSecretKey(),
                new IvParameterSpec(keyWithIv.getIv())
        );
        return cipher;
    }

    @SneakyThrows
    public Cipher decryption(SecretKeyWithIv keyWithIv) {
        Cipher cipher = Cipher.getInstance(encSpec.getCipherAlgo());
        cipher.init(
                Cipher.DECRYPT_MODE, keyWithIv.getSecretKey(),
                new IvParameterSpec(keyWithIv.getIv())
        );
        return cipher;
    }

    @SneakyThrows
    public SecretKeyWithIv generateKey() {
        byte[] iv = new byte[encSpec.getIvSize()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        KeyGenerator keyGen = KeyGenerator.getInstance(encSpec.getKeyAlgo());
        keyGen.init(encSpec.getLen());
        return new SecretKeyWithIv(iv, keyGen.generateKey());
    }

    @RequiredArgsConstructor
    public static class Encryption implements EncryptionServiceWithKey {

        @Getter
        private final String id;

        @Getter
        private final SecretKeyWithIv key;

        private final Supplier<Cipher> encryption;
        private final Supplier<Cipher> decryption;

        @Override
        @Synchronized("encryption")
        @SneakyThrows
        public byte[] encrypt(byte[] data) {
            return encryption.get().doFinal(data);
        }

        @Override
        @Synchronized("decryption")
        @SneakyThrows
        public byte[] decrypt(byte[] data) {
            return decryption.get().doFinal(data);
        }
    }
}
