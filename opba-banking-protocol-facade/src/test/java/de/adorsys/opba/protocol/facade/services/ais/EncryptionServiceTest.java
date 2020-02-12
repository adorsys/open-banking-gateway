package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.config.EncryptionConfig;
import de.adorsys.opba.protocol.facade.services.EncryptionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

import static de.adorsys.opba.protocol.facade.config.EncryptionConfig.ALGO;
import static de.adorsys.opba.protocol.facade.config.EncryptionConfig.ITER_COUNT;
import static de.adorsys.opba.protocol.facade.utils.EncryptionUtils.getNewSalt;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {EncryptionConfig.class, EncryptionServiceImpl.class})
public class EncryptionServiceTest {

    @Autowired
    EncryptionService encryptionService;

    @Test
    void encryptDecryptPasswordTest() {
        String password = "QwE!@#";
        byte[] salt = getNewSalt();

        SecretKey secretKey = encryptionService.generateKey(password, ALGO, salt, ITER_COUNT);
        byte[] encryptedSecretKey = encryptionService.encryptSecretKey(secretKey.getEncoded());

        byte[] decryptedSecretKey = encryptionService.decryptSecretKey(encryptedSecretKey);

        SecretKey newlyCreatedFromPassword = encryptionService.generateKey(password, ALGO, salt, ITER_COUNT);
        assertThat(decryptedSecretKey).isEqualTo(newlyCreatedFromPassword.getEncoded());
    }

    @Test
    void encryptDecryptDataTest() {
        String password = "password";
        String data = "data to encrypt";

        SecretKey key = encryptionService.generateKey(password, ALGO, getNewSalt(), ITER_COUNT);

        byte[] encryptedData = encryptionService.encrypt(data.getBytes(), key.getEncoded());

        byte[] decryptedData = encryptionService.decrypt(encryptedData, key.getEncoded());

        assertThat(decryptedData).isEqualTo(data.getBytes());
    }
}
