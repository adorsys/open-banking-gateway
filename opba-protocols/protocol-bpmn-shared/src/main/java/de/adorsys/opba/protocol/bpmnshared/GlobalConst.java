package de.adorsys.opba.protocol.bpmnshared;

import lombok.experimental.UtilityClass;

/**
 * Global constant names.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class GlobalConst {

    /**
     * Flowable context variable name.
     */
    public static final String CONTEXT = "CONTEXT";

    /**
     * Commonly used keyword for DTO mappers.
     */
    public static final String SPRING_KEYWORD = "spring";
}
