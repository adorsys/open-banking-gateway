package de.adorsys.opba.fintech.impl.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

@Data
@Validated
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("fintech-ui")
public class FintechUiConfig {

    @NotBlank
    private String redirectUrl;

    @NotBlank
    private String exceptionUrl;

    @NotBlank
    private String paymentOkRedirectUrl;

    @NotBlank
    private String paymentExceptionRedirectUrl;

    @NotBlank
    private String unauthorizedUrl;

    @NotNull
    private URI oauth2LoginCallbackUrl;
}
