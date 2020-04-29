package de.adorsys.opba.api.security.internal.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank
    private String jwsAlgo;
}
