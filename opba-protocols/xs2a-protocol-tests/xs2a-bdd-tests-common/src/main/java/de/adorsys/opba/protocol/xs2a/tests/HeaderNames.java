package de.adorsys.opba.protocol.xs2a.tests;

import lombok.experimental.UtilityClass;

/**
 * Additional header names.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class HeaderNames {

    public static final String FINTECH_ID = "Fintech-ID";
    public static final String BANK_ID = "Bank-ID";
    public static final String FINTECH_REDIRECT_URL_OK = "Fintech-Redirect-URL-OK";
    public static final String FINTECH_REDIRECT_URL_NOK = "Fintech-Redirect-URL-NOK";
    public static final String FINTECH_USER_ID = "Fintech-User-ID";
    public static final String SERVICE_SESSION_PASSWORD = "Service-Session-Password";
    public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";

    public static final String X_TIMESTAMP_UTC = "X-Timestamp-UTC";
    public static final String X_REQUEST_SIGNATURE = "X-Request-Signature";
    public static final String X_REQUEST_ID = "X-Request-ID";
}
