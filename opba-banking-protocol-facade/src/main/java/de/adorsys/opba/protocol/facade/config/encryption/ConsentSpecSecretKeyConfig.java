package de.adorsys.opba.protocol.facade.config.encryption;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Validated
@Configuration
@ConfigurationProperties("consent-spec.secret-key")
public class ConsentSpecSecretKeyConfig implements SymmetricEncSpec {

    @NotBlank
    private String keyAlgo;

    @Min(128)
    @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal value - at least 128 bit key
    private int len;

    @Min(0)
    @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal value
    private int ivSize;

    @NotBlank
    private String cipherAlgo;
}
