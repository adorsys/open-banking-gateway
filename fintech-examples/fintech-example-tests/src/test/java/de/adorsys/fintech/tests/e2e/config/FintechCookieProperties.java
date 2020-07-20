package de.adorsys.fintech.tests.e2e.config;

import de.adorsys.opba.api.security.internal.config.ConfigConst;
import de.adorsys.opba.api.security.internal.config.CookieProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = ConfigConst.API_CONFIG_PREFIX + "cookie")
public class FintechCookieProperties extends CookieProperties {
}
