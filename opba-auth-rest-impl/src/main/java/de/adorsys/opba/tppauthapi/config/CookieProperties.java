package de.adorsys.opba.tppauthapi.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Data
@Configuration
@ToString
@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {

    private boolean secure = false;
    private int maxAge = 300;
    private boolean httpOnly = true;
    private String path = "/";

    @Nullable
    private String sameSite;
}
