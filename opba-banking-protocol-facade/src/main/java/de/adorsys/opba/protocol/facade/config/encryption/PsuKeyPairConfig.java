package de.adorsys.opba.protocol.facade.config.encryption;

import lombok.Data;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static de.adorsys.opba.protocol.facade.config.ConfigConst.FACADE_CONFIG_PREFIX;

/**
 * PSU/Fintech user encryption key pair config.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(FACADE_CONFIG_PREFIX + "encryption.psu.key-pair")
public class PsuKeyPairConfig implements CmsEncSpec {

    /**
     * Key algorithm
     */
    @NotBlank
    private String keyAlgo;

    /**
     * Key length, bits.
     */
    @Min(64)
    @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal value - at least 64 bit key
    private int len;

    /**
     * Cipher algorithm.
     */
    @NotNull
    private ASN1ObjectIdentifier cipherAlgo;
}
