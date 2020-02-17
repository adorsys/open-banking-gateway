package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.services.FacadeEncryptionServiceFactory;
import de.adorsys.opba.protocol.facade.config.EncryptionConfig;
import de.adorsys.opba.protocol.facade.config.EncryptionProperties;
import de.adorsys.opba.protocol.facade.services.EncryptionServiceImpl;
import de.adorsys.opba.protocol.facade.services.SecretKeyOperationsImpl;
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
        FacadeEncryptionServiceFactory.class,
        SecretKeyOperationsImpl.class
})
@EnableConfigurationProperties
public class EncryptionServiceTest {

    @Autowired
    private FacadeEncryptionServiceFactory facadeEncryptionServiceFactory;
    @Autowired
    private SecretKeyOperations secretKeyOperations;
    @Autowired
    private EncryptionProperties properties;

    @Test
    void encryptDecryptPasswordTest() {
        String password = "QwE!@#";
        byte[] salt = getNewSalt(properties.getSaltLength());

        byte[] secretKey = secretKeyOperations.generateKey(password, salt);
        byte[] encryptedSecretKey = secretKeyOperations.encrypt(secretKey);

        byte[] decryptedSecretKey = secretKeyOperations.decrypt(encryptedSecretKey);

        byte[] newlyCreatedFromPassword = secretKeyOperations.generateKey(password, salt);
        assertThat(decryptedSecretKey).isEqualTo(newlyCreatedFromPassword);
    }

    @Test
    void encryptDecryptDataTest() {
        String password = "password";
        String data = "data to encrypt";

        byte[] key = secretKeyOperations.generateKey(password);
        EncryptionService encryptionService = facadeEncryptionServiceFactory.provideEncryptionService(key);
        byte[] encryptedData = encryptionService.encrypt(data.getBytes());

        byte[] decryptedData = encryptionService.decrypt(encryptedData);

        assertThat(decryptedData).isEqualTo(data.getBytes());
    }
}
