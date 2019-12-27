package de.adorsys.opba.core.protocol.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class GlobalConst {

    public static final String CONTEXT = "CONTEXT";
    public static final String RESULT = "RESULT";

    public static final String REQUEST_SAGA = "request-saga";
    public static final String VALIDATION_ERROR_CODE = "VALIDATION";
    public static final String VALIDATIONS_RESULT_HEADER = "X-VALIDATION";
}
