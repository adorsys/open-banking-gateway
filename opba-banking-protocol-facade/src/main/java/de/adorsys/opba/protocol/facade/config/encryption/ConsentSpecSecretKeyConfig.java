package de.adorsys.opba.protocol.facade.config.encryption;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import static de.adorsys.opba.protocol.facade.config.ConfigConst.FACADE_CONFIG_PREFIX;

/**
 * Secret key configuration for consent specification encryption.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(FACADE_CONFIG_PREFIX + "encryption.consent-spec.secret-key")
public class ConsentSpecSecretKeyConfig implements SymmetricEncSpec {

    /**
     * Secret key algorithm.
     */
    @NotBlank
    private String keyAlgo;

    /**
     * Secret key length, bits
     */
    @Min(128)
    @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal value - at least 128 bit key
    private int len;

    /**
     * Initialization vector size, bytes
     */
    @Min(0)
    @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal value
    private int ivSize;

    /**
     * Cipher algorithm.
     */
    @NotBlank
    private String cipherAlgo;
}
