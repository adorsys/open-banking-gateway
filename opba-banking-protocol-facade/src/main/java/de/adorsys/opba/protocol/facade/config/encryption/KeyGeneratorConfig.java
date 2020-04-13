package de.adorsys.opba.protocol.facade.config.encryption;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Configuration
@RequiredArgsConstructor
public class KeyGeneratorConfig {

    @Bean
    PsuSecretKeyGenerator psuSecretKeyGenerator(PsuSecretKeyConfig secretKeyConfig) {
        return new PsuSecretKeyGenerator(secretKeyConfig);
    }

    @Bean
    ConsentSpecSecretKeyGenerator consentSpecSecretKeyGenerator(ConsentSpecSecretKeyConfig secretKeyConfig) {
        return new ConsentSpecSecretKeyGenerator(secretKeyConfig);
    }

    @RequiredArgsConstructor
    public static class PsuSecretKeyGenerator {

        private final PsuSecretKeyConfig config;

        @SneakyThrows
        public SecretKey generate() {
            KeyGenerator keyGen = KeyGenerator.getInstance(config.getAlgo());
            keyGen.init(config.getLen());
            return keyGen.generateKey();
        }
    }

    @RequiredArgsConstructor
    public static class ConsentSpecSecretKeyGenerator {

        private final ConsentSpecSecretKeyConfig config;

        @SneakyThrows
        public SecretKey generate() {
            KeyGenerator keyGen = KeyGenerator.getInstance(config.getAlgo());
            keyGen.init(config.getLen());
            return keyGen.generateKey();
        }
    }
}
