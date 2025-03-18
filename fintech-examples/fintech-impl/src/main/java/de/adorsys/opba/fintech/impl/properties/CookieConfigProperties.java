package de.adorsys.opba.fintech.impl.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ToString
@ConfigurationProperties(prefix = "server.controller")
public class CookieConfigProperties {

    @NotNull
    private CookieConfigPropertiesSpecific sessioncookie;

    @NotNull
    private CookieConfigPropertiesSpecific redirectcookie;

    @NotNull
    private CookieConfigPropertiesSpecific oauth2cookie;
}
