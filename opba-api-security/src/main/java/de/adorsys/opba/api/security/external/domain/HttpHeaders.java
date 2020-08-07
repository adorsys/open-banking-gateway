package de.adorsys.opba.api.security.external.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") //Checkstyle doesn't recognise Lombok
public class HttpHeaders {

    public static final String SERVICE_SESSION_ID = "Service-Session-ID";
    public static final String X_REQUEST_ID = "X-Request-ID";

    public static final String X_TIMESTAMP_UTC = "X-Timestamp-UTC";
    public static final String X_REQUEST_SIGNATURE = "X-Request-Signature";
    public static final String FINTECH_ID = "Fintech-ID";
    public static final String AUTHORIZATION_SESSION_KEY = "Authorization-Session-Key";
}
