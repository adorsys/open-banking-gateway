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

@Data
@Validated
@Configuration
@ConfigurationProperties(FACADE_CONFIG_PREFIX + "encryption.fintech-only.key-pair")
public class FintechOnlyKeyPairConfig implements CmsEncSpec {

    @Min(1)
    private int pairCount;

    @NotBlank
    private String keyAlgo;

    @Min(64)
    @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal value - at least 64 bit key
    private int len;

    @NotNull
    private ASN1ObjectIdentifier cipherAlgo;
}
