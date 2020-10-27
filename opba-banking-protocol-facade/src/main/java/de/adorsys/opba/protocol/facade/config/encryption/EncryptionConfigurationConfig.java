package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.datasafe.encrypiton.api.types.encryption.MutableEncryptionConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfigurationConfig {

    @Bean
    @ConfigurationProperties(prefix = "facade.datasafe")
    public MutableEncryptionConfig encryptionConfig() {
        return new MutableEncryptionConfig();
    }
}
