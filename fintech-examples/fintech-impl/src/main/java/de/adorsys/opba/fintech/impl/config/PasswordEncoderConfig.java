package de.adorsys.opba.fintech.impl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder(BCryptProperties cryptProperties) {
        return new BCryptPasswordEncoder(
                cryptProperties.getVersion(),
                cryptProperties.getStrength()
        );
    }

    @Data
    @Validated
    @Configuration
    @ConfigurationProperties("security.user.password.encryption")
    public static class BCryptProperties {

        @NotNull
        private BCryptPasswordEncoder.BCryptVersion version;

        @Min(12)
        @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal strength
        private int strength;
    }
}
