package de.adorsys.opba.protocol.facade.config.encryption;

import lombok.Data;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static de.adorsys.opba.protocol.facade.config.ConfigConst.FACADE_CONFIG_PREFIX;

/**
 * Fintech-only encryption configuration.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(FACADE_CONFIG_PREFIX + "encryption.fintech-only.key-pair")
public class FintechOnlyKeyPairConfig implements CmsEncSpec {

    /**
     * Count of key-pairs in KeyStore.
     */
    @Min(1)
    private int pairCount;

    /**
     * Key algorithm.
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
     * Encryption/decryption algorithm.
     */
    @NotNull
    private ASN1ObjectIdentifier cipherAlgo;
}
