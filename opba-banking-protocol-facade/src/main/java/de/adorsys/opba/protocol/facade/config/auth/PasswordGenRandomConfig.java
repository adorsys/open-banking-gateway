package de.adorsys.opba.protocol.facade.config.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class PasswordGenRandomConfig {

    @Bean
    FintechUserPasswordGenRandom fintechUserPasswordGenRandom() {
        return new FintechUserPasswordGenRandom(new SecureRandom());
    }

    @Getter
    @RequiredArgsConstructor
    public static class FintechUserPasswordGenRandom {

        private final SecureRandom random;
    }
}
