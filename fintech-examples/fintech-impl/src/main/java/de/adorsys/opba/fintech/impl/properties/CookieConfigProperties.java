package de.adorsys.opba.fintech.impl.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Data
@Configuration
@ConfigurationProperties(prefix = "server.controller.cookie")
public class CookieConfigProperties {
    private static final Integer MAX_AGE_DEFAULT = 300;
    private boolean secure = false;
    private int maxAge = MAX_AGE_DEFAULT;
    private boolean httpOnly = false;
    private String path = "/";

    @Nullable
    private String sameSite;
}
