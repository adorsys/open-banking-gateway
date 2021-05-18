package de.adorsys.opba.protocol.xs2a.constant;

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
     * Flowable variable name to carry validation issues (like missing PSU ID).
     */
    public static final String LAST_VALIDATION_ISSUES = "LAST_VALIDATION_ISSUES";

    /**
     * Flowable variable name to carry last redirection target (where it was decided to redirect user to last time).
     */
    public static final String LAST_REDIRECTION_TARGET = "LAST_REDIRECTION_TARGET";

    /**
     * Flowable variable name to store context that was before validation (as validation modifies context).
     */
    public static final String BEFORE_VALIDATION_CONTEXT = "BEFORE_VALIDATION_CONTEXT";

    /**
     * General request handler flowable process.
     */
    public static final String XS2A_REQUEST_SAGA = "xs2a-request-saga";

    /**
     * Validation error exception code.
     */
    public static final String VALIDATION_ERROR_CODE = "VALIDATION";

    /**
     * Package for generated mappers for DTOs.
     */
    public static final String XS2A_MAPPERS_PACKAGE = "de.adorsys.opba.protocol.xs2a.service.mappers.generated";

    /**
     * Commonly used keyword for DTO mappers.
     */
    public static final String SPRING_KEYWORD = "spring";

    /**
     * ING-specific xs2a Adapter peculiarity.
     */
    public static final String OAUTH_CONSENT = "oauthConsent";
}
