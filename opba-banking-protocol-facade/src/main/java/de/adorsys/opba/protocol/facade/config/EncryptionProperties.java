package de.adorsys.opba.protocol.facade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static de.adorsys.opba.protocol.api.Profiles.NO_ENCRYPTION;

// FIXME - Drop it
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "facade.encryption")
@Profile("!" + NO_ENCRYPTION)
public class EncryptionProperties {
    @NotNull
    private String providerName;
    @NotNull
    private String algorithm;
    @Min(1)
    private int saltLength;
    @Min(0)
    private int iterationCount;
    @NotNull
    private String keySetPath;
}
