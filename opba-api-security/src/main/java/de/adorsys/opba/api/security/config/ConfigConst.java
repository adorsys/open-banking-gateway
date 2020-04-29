package de.adorsys.opba.api.security.config;

import lombok.experimental.UtilityClass;

/**
 * Configuration constant names.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ConfigConst {

    /**
     * API security {@link org.springframework.boot.context.properties.ConfigurationProperties} prefix.
     */
    public static final String API_CONFIG_PREFIX = "api.";
}
