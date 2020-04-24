package de.adorsys.opba.protocol.api.dto.headers;

import lombok.experimental.UtilityClass;

/**
 * Headers that can additionally expand FacadeResponse.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ResponseHeaders {

    /**
     * Message of the error that occurred.
     */
    public static final String X_ERROR_MESSAGE = "X-ERROR-MESSAGE";

    /**
     * Machine-parsable code describing the error.
     */
    public static final String X_ERROR_CODE = "X-ERROR-CODE";
}
