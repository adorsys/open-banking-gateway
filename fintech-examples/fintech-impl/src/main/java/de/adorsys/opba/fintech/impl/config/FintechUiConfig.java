package de.adorsys.opba.fintech.impl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("fintech-ui")
public class FintechUiConfig {

    private String redirectUrl;
    private String exceptionUrl;
    private String unauthorizedUrl;
}
