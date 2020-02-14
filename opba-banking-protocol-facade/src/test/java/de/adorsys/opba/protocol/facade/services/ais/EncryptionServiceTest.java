package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.SecretKeyService;
import de.adorsys.opba.protocol.facade.FacadeEncryptionService;
import de.adorsys.opba.protocol.facade.config.EncryptionConfig;
import de.adorsys.opba.protocol.facade.config.EncryptionProperties;
import de.adorsys.opba.protocol.facade.services.EncryptionServiceImpl;
import de.adorsys.opba.protocol.facade.services.SecretKeyServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static de.adorsys.opba.protocol.facade.utils.EncryptionUtils.getNewSalt;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {
        EncryptionConfig.class,
        EncryptionServiceImpl.class,
        EncryptionProperties.class,
        FacadeEncryptionService.class,
        SecretKeyServiceImpl.class
})
@EnableConfigurationProperties
public class EncryptionServiceTest {

    @Autowired
    private FacadeEncryptionService facadeEncryptionService;
    @Autowired
    private SecretKeyService secretKeyService;
    @Autowired
    private EncryptionProperties properties;

    @Test
    void encryptDecryptPasswordTest() {
        String password = "QwE!@#";
        byte[] salt = getNewSalt(properties.getSaltLength());

        byte[] secretKey = secretKeyService.generateKey(password, salt);
        byte[] encryptedSecretKey = secretKeyService.encrypt(secretKey);

        byte[] decryptedSecretKey = secretKeyService.decrypt(encryptedSecretKey);

        byte[] newlyCreatedFromPassword = secretKeyService.generateKey(password, salt);
        assertThat(decryptedSecretKey).isEqualTo(newlyCreatedFromPassword);
    }

    @Test
    void encryptDecryptDataTest() {
        String password = "password";
        String data = "data to encrypt";

        byte[] key = secretKeyService.generateKey(password);
        EncryptionService encryptionService = facadeEncryptionService.provideEncryptionService(key);
        byte[] encryptedData = encryptionService.encrypt(data.getBytes());

        byte[] decryptedData = encryptionService.decrypt(encryptedData);

        assertThat(decryptedData).isEqualTo(data.getBytes());
    }
}
