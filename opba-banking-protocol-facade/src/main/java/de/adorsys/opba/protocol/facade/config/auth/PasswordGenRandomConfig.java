package de.adorsys.opba.protocol.facade.config.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

/**
 * Configuration for Fintech-User random Keystore password generation.
 */
@Configuration
public class PasswordGenRandomConfig {

    @Bean
    FintechUserPasswordGenRandom fintechUserPasswordGenRandom() {
        return new FintechUserPasswordGenRandom(new SecureRandom());
    }

    /**
     * Fintech user (intermediate) password generator.
     */
    @Getter
    @RequiredArgsConstructor
    public static class FintechUserPasswordGenRandom {

        private final SecureRandom random;
    }
}
