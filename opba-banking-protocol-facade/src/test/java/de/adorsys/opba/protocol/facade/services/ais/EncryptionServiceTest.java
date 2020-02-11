package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.facade.services.EncryptionService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptionServiceTest {

    EncryptionService encryptionService = new EncryptionService();

    @Test
    void encryptDecryptPasswordTest() {
        String password = "QwE!@#";
        byte[] encryptedPassword = encryptionService.encryptPassword(password);

        byte[] decryptedPassword = encryptionService.decryptPassword(encryptedPassword);

        assertThat(decryptedPassword).isEqualTo(password.getBytes());
    }

    @Test
    void encryptDecryptDataTest() {
        String password = "password";
        String data = "data to encrypt";

        byte[] encryptedData = encryptionService.encrypt(data.getBytes(), password);

        byte[] decryptedData = encryptionService.decrypt(encryptedData, password);

        assertThat(decryptedData).isEqualTo(data.getBytes());
    }
}
