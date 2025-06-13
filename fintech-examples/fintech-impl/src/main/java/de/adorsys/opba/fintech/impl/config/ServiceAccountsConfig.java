package de.adorsys.opba.fintech.impl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Validated
@Configuration
@ConfigurationProperties("service-accounts")
public class ServiceAccountsConfig {

    private List<@Valid ServiceAccount> accounts;

    @Data
    public static class ServiceAccount {

        @NotBlank
        private String login;

        @NotBlank
        @Size(min = 12)
        @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal password size, we want long service account passwords
        private String password;
    }
}
