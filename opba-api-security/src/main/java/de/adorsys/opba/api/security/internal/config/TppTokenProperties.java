package de.adorsys.opba.api.security.internal.config;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

@Data
public class TppTokenProperties {

    @NotBlank
    private String privateKey;

    @NotBlank
    private String publicKey;

    @NotBlank
    private String signAlgo;

    @NotNull
    private Duration tokenValidityDuration;

    @NotNull
    private Duration redirectTokenValidityDuration;

    @NotBlank
    private String jwsAlgo;
}
