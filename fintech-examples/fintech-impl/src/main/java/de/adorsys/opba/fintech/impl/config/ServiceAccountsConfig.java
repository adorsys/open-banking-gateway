package de.adorsys.opba.fintech.impl.config;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
        @Length(min = 12)
        @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal password size, we want long service account passwords
        private String password;
    }
}
