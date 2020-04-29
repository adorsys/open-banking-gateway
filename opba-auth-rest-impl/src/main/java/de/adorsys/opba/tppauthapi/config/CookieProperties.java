package de.adorsys.opba.tppauthapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

import static de.adorsys.opba.tppbankingapi.config.ConfigConst.API_CONFIG_PREFIX;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = API_CONFIG_PREFIX + "cookie")
public class CookieProperties {
    private boolean secure = true;
    private boolean httpOnly = true;
    private String path = "/";

    @NotNull
    private Duration maxAge;

    @NotBlank
    private String sameSite;
}
