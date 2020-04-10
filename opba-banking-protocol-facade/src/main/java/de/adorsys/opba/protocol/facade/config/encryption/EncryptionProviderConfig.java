package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.opba.protocol.api.services.EncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.NoEncryptionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// FIXME - replace with normal encryption
@Configuration
public class EncryptionProviderConfig {

    @Bean
    EncryptionServiceProvider dummyEncryption() {
        return id -> new NoEncryptionServiceImpl();
    }
}
