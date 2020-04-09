package de.adorsys.opba.api.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "security.tpp")
public class TppTokenProperties {

    @NotBlank
    private String privateKey;

    @NotBlank
    private String publicKey;

    @NotBlank
    private String signAlgo;

    @NotNull
    private Duration keyValidityDuration;

    @NotBlank
    private String jwsAlgo;
}
