package de.adorsys.opba.fintech.impl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties("security.user.registration")
public class UserRegistrationConfiguration {

    /**
     * Indicates if FinTech has open user registration (just type not yet present credentials to get registered)
     */
    @NotNull
    private UserRegistrationConfiguration.SecurityState simple;

    public enum SecurityState {
        ALLOW,
        DENY
    }
}
