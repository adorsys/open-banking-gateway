package de.adorsys.opba.fintech.impl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Data
@Validated
@Configuration
@ConfigurationProperties("oauth2.login.gmail")
public class GmailOauth2Config implements Oauth2Config {

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotNull
    private URI authenticationEndpoint;

    @NotNull
    private URI codeToTokenEndpoint;

    @NotEmpty
    private List<@NotBlank String> scope;

    @NotEmpty
    private List<@NotBlank String> allowedEmailsRegex;
}
