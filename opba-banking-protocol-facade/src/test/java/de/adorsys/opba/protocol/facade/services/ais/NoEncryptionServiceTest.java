package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import de.adorsys.opba.protocol.facade.services.FacadeEncryptionServiceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static de.adorsys.opba.protocol.api.Profiles.NO_ENCRYPTION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note: This test keeps DB in dirty state - doesn't cleanup after itself.
 */
@ActiveProfiles(NO_ENCRYPTION)
@SpringBootTest(classes = ApplicationTest.class)
public class NoEncryptionServiceTest {
    public static final byte[] TEST_DATA = "123".getBytes();

    @Autowired
    private FacadeEncryptionServiceFactory facadeEncryptionServiceFactory;

    @Autowired
    private SecretKeyOperations secretKeyOperations;

    @Test
    void noEncryption() {
        EncryptionService encryptionService = facadeEncryptionServiceFactory.provideEncryptionService(null);
        assertThat(encryptionService.encrypt(TEST_DATA)).isEqualTo(TEST_DATA);
        assertThat(encryptionService.decrypt(TEST_DATA)).isEqualTo(TEST_DATA);
    }

    @Test
    void noKeyEncryption() {
        assertThat(secretKeyOperations.encrypt(TEST_DATA)).isEqualTo(TEST_DATA);
        assertThat(secretKeyOperations.decrypt(TEST_DATA)).isEqualTo(TEST_DATA);
    }
}
