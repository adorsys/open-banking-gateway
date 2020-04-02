package de.adorsys.opba.fintech.impl.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("fintech-ui")
public class FintechUiConfig {

    private String redirectUrl;
    private String exceptionUrl;
    private String unauthorizedUrl;
}
