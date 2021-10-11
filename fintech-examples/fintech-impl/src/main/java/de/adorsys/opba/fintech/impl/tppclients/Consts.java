package de.adorsys.opba.fintech.impl.tppclients;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Consts {
    public static final String COOKIE_SESSION_COOKIE_NAME = "SESSION-COOKIE";
    public static final String COOKIE_REDIRECT_COOKIE_NAME = "REDIRECT-COOKIE";
    public static final String HEADER_XSRF_TOKEN = "X-XSRF-TOKEN";
    public static final String HEADER_SESSION_MAX_AGE = "X-SESSION-MAX-AGE";
    public static final String HEADER_REDIRECT_MAX_AGE = "X-REDIRECT-MAX-AGE";
    public static final String HEADER_X_REQUEST_ID = "X-REQUEST-ID";
    public static final Boolean HEADER_COMPUTE_PSU_IP_ADDRESS = true;

    // Actual values are set in feign request interceptor (FeignConfig.java)
    public static final String COMPUTE_X_TIMESTAMP_UTC = null;
    public static final String COMPUTE_X_REQUEST_SIGNATURE = null;
    public static final String COMPUTE_FINTECH_ID = null;
}
