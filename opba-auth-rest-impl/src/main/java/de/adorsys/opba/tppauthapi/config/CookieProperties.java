package de.adorsys.opba.tppauthapi.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@Configuration
@ToString
@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {

    private boolean secure = true;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration maxAgeSeconds = Duration.ofSeconds(300);
    private boolean httpOnly = true;
    private String path = "/";
    private String sameSite;
}
