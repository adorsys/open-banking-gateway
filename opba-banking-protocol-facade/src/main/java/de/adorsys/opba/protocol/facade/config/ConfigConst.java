package de.adorsys.opba.protocol.facade.config;

import lombok.experimental.UtilityClass;

/**
 * Configuration constant names.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ConfigConst {

    /**
     * Facade {@link org.springframework.boot.context.properties.ConfigurationProperties} prefix.
     */
    public static final String FACADE_CONFIG_PREFIX = "facade.";
}
