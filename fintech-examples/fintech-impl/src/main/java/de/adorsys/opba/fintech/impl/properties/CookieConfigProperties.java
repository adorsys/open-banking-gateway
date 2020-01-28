package de.adorsys.opba.fintech.impl.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "server.servlet.session.cookie")
public class CookieConfigProperties {
    private static final Integer MAX_AGE_DEFAULT = 300;
    private boolean secure = false;
    private int maxAge = MAX_AGE_DEFAULT;
    private boolean httpOnly = false;
    private String path = "/";

    @Nullable
    private String sameSite;
}
