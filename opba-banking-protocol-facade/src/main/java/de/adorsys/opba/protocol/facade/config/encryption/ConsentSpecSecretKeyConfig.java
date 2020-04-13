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
public class ConsentSpecSecretKeyConfig {

    @NotBlank
    private String algo;

    @Min(128)
    private int len;
}
