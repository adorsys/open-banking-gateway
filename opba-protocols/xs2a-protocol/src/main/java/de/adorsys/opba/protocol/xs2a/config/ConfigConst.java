package de.adorsys.opba.protocol.xs2a.config;

import lombok.experimental.UtilityClass;

/**
 * Configuration constant names.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ConfigConst {

    /**
     * XS2A protocol {@link org.springframework.boot.context.properties.ConfigurationProperties} prefix.
     */
    public static final String XS2A_PROTOCOL_CONFIG_PREFIX = "protocol.xs2a.";
}
