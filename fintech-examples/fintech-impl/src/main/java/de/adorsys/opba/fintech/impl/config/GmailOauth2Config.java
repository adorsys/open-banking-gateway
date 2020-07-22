package de.adorsys.opba.fintech.impl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Data
@Validated
@Configuration
@ConfigurationProperties("oauth2.login.gmail")
public class GmailOauth2Config {

    @NotBlank
    private String clientId;

    @NotNull
    private URI authenticationEndpoint;

    @NotBlank
    private String scope;
}
