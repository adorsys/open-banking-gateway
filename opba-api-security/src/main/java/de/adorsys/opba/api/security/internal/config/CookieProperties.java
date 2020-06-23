package de.adorsys.opba.api.security.internal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = ConfigConst.API_CONFIG_PREFIX + "cookie")
public class CookieProperties {
    private boolean secure = true;
    private boolean httpOnly = true;
    private String path = "/";
    private String redirectPathTemplate = "/";

    @NotNull
    private Duration maxAge;

    @NotNull
    private Duration redirectMaxAge;

    @NotBlank
    private String sameSite;
}
