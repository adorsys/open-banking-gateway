package de.adorsys.opba.tppbankingapi.config;

import lombok.experimental.UtilityClass;

/**
 * Configuration constant names.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ConfigConst {

    /**
     * REST API {@code org.springframework.boot.context.properties.ConfigurationProperties} prefix.
     */
    public static final String BANKING_API_CONFIG_PREFIX = "api.banking.";
}
