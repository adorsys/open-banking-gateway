package de.adorsys.opba.fintech.impl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties("security.user.registration")
public class UserRegistrationConfig {

    /**
     * Indicates if FinTech has open user registration (just type not yet present credentials to get registered)
     */
    @NotNull
    private UserRegistrationConfig.SecurityState simple;

    public enum SecurityState {
        ALLOW,
        DENY
    }
}
