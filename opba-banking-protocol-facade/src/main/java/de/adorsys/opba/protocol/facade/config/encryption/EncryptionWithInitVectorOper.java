package de.adorsys.opba.protocol.facade.config.encryption;

import com.google.common.hash.Hashing;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

@RequiredArgsConstructor
public class EncryptionWithInitVectorOper {

    private final EncSpec encSpec;

    public EncryptionService encryptionService(SecretKeyWithIv keyWithIv) {
        return new Encryption(
                encryption(keyWithIv),
                decryption(keyWithIv),
                Hashing.sha256().hashBytes(keyWithIv.getSecretKey().getEncoded()).toString()
        );
    }

    @SneakyThrows
    public Cipher encryption(SecretKeyWithIv keyWithIv) {
        Cipher cipher = Cipher.getInstance(encSpec.getCipherAlgo());
        cipher.init(
                Cipher.ENCRYPT_MODE, keyWithIv.getSecretKey(),
                new GCMParameterSpec(keyWithIv.getIv().length, keyWithIv.getIv())
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
    public static class Encryption implements EncryptionService {

        private final Cipher encryption;
        private final Cipher decryption;

        @Getter
        private final String id;

        @Override
        @Synchronized("encryption")
        @SneakyThrows
        public synchronized byte[] encrypt(byte[] data) {
            return encryption.doFinal(data);
        }

        @Override
        @Synchronized("decryption")
        @SneakyThrows
        public byte[] decrypt(byte[] data) {
            return decryption.doFinal(data);
        }
    }
}
