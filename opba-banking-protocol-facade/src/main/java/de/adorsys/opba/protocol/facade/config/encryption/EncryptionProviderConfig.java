package de.adorsys.opba.protocol.facade.config.encryption;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * High level encryption provider that abstracts away encryption details.
 */
@Configuration
public class EncryptionProviderConfig {

    /**
     * Consent authorization flow encryption.
     * @param specSecretKeyConfig Secret key based encryption configuration.
     * @return Consent Authorization encryption
     */
    @Bean
    ConsentAuthorizationEncryptionServiceProvider consentAuthEncryptionProvider(ConsentSpecSecretKeyConfig specSecretKeyConfig) {
        return new ConsentAuthorizationEncryptionServiceProvider(
                new EncryptionWithInitVectorOper(specSecretKeyConfig)
        );
    }

    /**
     * PSU/Fintech user consent encryption.
     * @param psuKeyPairConfig Asymmetric encryption key configuration.
     * @return PSU/Fintech user data encryption
     */
    @Bean
    PsuEncryptionServiceProvider psuConsentEncryptionProvider(PsuKeyPairConfig psuKeyPairConfig) {
        return new PsuEncryptionServiceProvider(new CmsEncryptionOper(psuKeyPairConfig));
    }

    /**
     * Fintech data and consent access encryption.
     * @param fintechOnlyKeyPairConfig Asymmetric encryption key configuration.
     * @return Fintech data encryption
     */
    @Bean
    FintechOnlyEncryptionServiceProvider fintechOnlyEncryptionProvider(FintechOnlyKeyPairConfig fintechOnlyKeyPairConfig) {
        return new FintechOnlyEncryptionServiceProvider(new CmsEncryptionOper(fintechOnlyKeyPairConfig));
    }
}
