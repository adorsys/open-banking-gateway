package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import lombok.experimental.UtilityClass;

/**
 * Configuration constant names.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ConfigConst {

    /**
     * Flowable protocol engine {@link org.springframework.boot.context.properties.ConfigurationProperties} prefix.
     */
    public static final String FLOWABLE_SHARED_CONFIG_PREFIX = "bpmnshared.flowable";
}
