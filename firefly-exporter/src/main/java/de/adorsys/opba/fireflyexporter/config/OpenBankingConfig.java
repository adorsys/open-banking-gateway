package de.adorsys.opba.fireflyexporter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URL;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "open-banking")
public class OpenBankingConfig {

    @NotNull
    private URL url;

    @NotBlank
    private String clientId;

    @NotBlank
    private String dataProtectionPassword;

    @NotBlank
    private String userId;
}
