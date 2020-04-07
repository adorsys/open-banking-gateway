package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.dto.KeyDto;
import de.adorsys.opba.protocol.api.dto.KeyWithParamsDto;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import de.adorsys.opba.protocol.facade.services.FacadeEncryptionServiceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note: This test keeps DB in dirty state - doesn't cleanup after itself.
 */
@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
public class EncryptionServiceTest {

    @Autowired
    private SecretKeyOperations secretKeyOperations;

    @Autowired
    private FacadeEncryptionServiceFactory facadeEncryptionServiceFactory;

    @Test
    void encryptDecryptPasswordTest() {
        String password = "QwE!@#";

        KeyWithParamsDto keyWithParams = secretKeyOperations.generateKey(password);
        byte[] encryptedSecretKey = secretKeyOperations.encrypt(keyWithParams.getKey());

        byte[] decryptedSecretKey = secretKeyOperations.decrypt(encryptedSecretKey);

        KeyWithParamsDto reCreatedFromPassword = secretKeyOperations.generateKey(
                password,
                keyWithParams.getAlgorithm(),
                keyWithParams.getSalt(),
                keyWithParams.getIterationCount());
        assertThat(decryptedSecretKey).isEqualTo(reCreatedFromPassword.getKey());
    }

    @Test
    void encryptDecryptDataTest() {
        String password = "password";
        String data = "data to encrypt";

        KeyDto key = secretKeyOperations.generateKey(password);
        EncryptionService encryptionService = facadeEncryptionServiceFactory.provideEncryptionService(UUID.randomUUID(), key.getKey());
        byte[] encryptedData = encryptionService.encrypt(data.getBytes());
        assertThat(encryptedData).isNotEqualTo(data.getBytes());

        byte[] decryptedData = encryptionService.decrypt(encryptedData);

        assertThat(decryptedData).isEqualTo(data.getBytes());
    }
}
