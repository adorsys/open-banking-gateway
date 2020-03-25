package de.adorsys.opba.fintech.impl.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Data
@Configuration
@ToString
@ConfigurationProperties(prefix = "server.controller")
public class CookieConfigProperties {
    @Nullable
    private CookieConfigPropertiesSpecific sessioncookie;
    @Nullable
    private CookieConfigPropertiesSpecific redirectcookie;
}
