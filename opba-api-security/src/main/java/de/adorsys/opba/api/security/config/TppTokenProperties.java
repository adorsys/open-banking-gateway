package de.adorsys.opba.api.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

import static de.adorsys.opba.api.security.config.ConfigConst.API_CONFIG_PREFIX;

@Data
@Validated
@Configuration
@ConfigurationProperties(API_CONFIG_PREFIX + "security")
public class TppTokenProperties {

    @NotBlank
    private String privateKey;

    @NotBlank
    private String publicKey;

    @NotBlank
    private String signAlgo;

    @NotNull
    private Duration tokenValidityDuration;

    @NotBlank
    private String jwsAlgo;
}
