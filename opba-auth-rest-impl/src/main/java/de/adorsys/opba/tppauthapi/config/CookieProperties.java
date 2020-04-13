package de.adorsys.opba.tppauthapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {
    private static final Integer MAX_AGE_DEFAULT = 300;

    private boolean secure = true;
    private Duration maxAge = Duration.ofSeconds(MAX_AGE_DEFAULT);
    private boolean httpOnly = true;
    private String path = "/";
    private String sameSite;
}
