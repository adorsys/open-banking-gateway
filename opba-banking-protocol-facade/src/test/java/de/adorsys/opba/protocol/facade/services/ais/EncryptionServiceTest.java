package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.config.FacadeConfig;
import de.adorsys.opba.protocol.facade.services.EncryptionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {FacadeConfig.class, EncryptionServiceImpl.class})
public class EncryptionServiceTest {

    @Autowired
    EncryptionService encryptionService;

    @Test
    void encryptDecryptPasswordTest() {
        String password = "QwE!@#";
        SecretKey secretKey = encryptionService.deriveKey(password);
        byte[] encryptedSecretKey = encryptionService.encryptSecretKey(secretKey);

        byte[] decryptedSecretKey = encryptionService.decryptSecretKey(encryptedSecretKey);

        SecretKey newlyCreatedFromPassword = encryptionService.deriveKey(password);
        assertThat(decryptedSecretKey).isEqualTo(newlyCreatedFromPassword.getEncoded());
    }

    @Test
    void encryptDecryptDataTest() {
        String password = "password";
        String data = "data to encrypt";

        SecretKey key = encryptionService.deriveKey(password);

        byte[] encryptedData = encryptionService.encrypt(data.getBytes(), key.getEncoded());

        byte[] decryptedData = encryptionService.decrypt(encryptedData, key.getEncoded());

        assertThat(decryptedData).isEqualTo(data.getBytes());
    }
}
