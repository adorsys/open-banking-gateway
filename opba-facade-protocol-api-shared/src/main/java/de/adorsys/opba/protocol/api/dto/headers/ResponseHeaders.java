package de.adorsys.opba.protocol.api.dto.headers;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ResponseHeaders {

    public static final String X_ERROR_MESSAGE = "X-ERROR-MESSAGE";
    public static final String X_ERROR_CODE = "X-ERROR-CODE";
}
