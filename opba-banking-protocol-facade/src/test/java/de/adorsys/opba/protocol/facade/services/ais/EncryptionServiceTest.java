package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.config.EncryptionConfig;
import de.adorsys.opba.protocol.facade.config.EncryptionProperties;
import de.adorsys.opba.protocol.facade.services.EncryptionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

import static de.adorsys.opba.protocol.facade.utils.EncryptionUtils.getNewSalt;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {EncryptionConfig.class, EncryptionServiceImpl.class})
@RequiredArgsConstructor
public class EncryptionServiceTest {

    private final EncryptionService encryptionService;
    private final EncryptionProperties properties;

    @Test
    void encryptDecryptPasswordTest() {
        String password = "QwE!@#";
        byte[] salt = getNewSalt(properties.getSaltLength());

        SecretKey secretKey = encryptionService.generateKey(password, salt);
        byte[] encryptedSecretKey = encryptionService.encryptSecretKey(secretKey.getEncoded());

        byte[] decryptedSecretKey = encryptionService.decryptSecretKey(encryptedSecretKey);

        SecretKey newlyCreatedFromPassword = encryptionService.generateKey(password, salt);
        assertThat(decryptedSecretKey).isEqualTo(newlyCreatedFromPassword.getEncoded());
    }

    @Test
    void encryptDecryptDataTest() {
        String password = "password";
        String data = "data to encrypt";

        SecretKey key = encryptionService.generateKey(password);

        byte[] encryptedData = encryptionService.encrypt(data.getBytes(), key.getEncoded());

        byte[] decryptedData = encryptionService.decrypt(encryptedData, key.getEncoded());

        assertThat(decryptedData).isEqualTo(data.getBytes());
    }
}
