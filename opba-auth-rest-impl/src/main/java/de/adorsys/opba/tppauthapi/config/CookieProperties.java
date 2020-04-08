package de.adorsys.opba.tppauthapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {
    private static final Integer MAX_AGE_DEFAULT = 300;

    private boolean secure = true;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration maxAgeSeconds = Duration.ofSeconds(MAX_AGE_DEFAULT);
    private boolean httpOnly = true;
    private String path = "/";
    private String sameSite;
}
