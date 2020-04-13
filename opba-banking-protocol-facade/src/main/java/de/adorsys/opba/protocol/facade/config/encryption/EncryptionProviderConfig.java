package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.EncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.NoEncryptionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

// FIXME - replace with normal encryption
@Configuration
public class EncryptionProviderConfig {

    @Bean
    EncryptionServiceProvider dummyEncryption() {
        return new EncryptionServiceProvider() {
            @Override
            public EncryptionService getEncryptionById(String id) {
                return new NoEncryptionServiceImpl();
            }

            @Override
            public EncryptionService forSecretKey(SecretKey key) {
                return new NoEncryptionServiceImpl();
            }
        };
    }
}
