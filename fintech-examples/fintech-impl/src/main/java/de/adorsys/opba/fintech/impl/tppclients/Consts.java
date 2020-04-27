package de.adorsys.opba.fintech.impl.tppclients;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Consts {
    public static final String COOKIE_SESSION_COOKIE_NAME = "SESSION-COOKIE";
    public static final String HEADER_XSRF_TOKEN = "X-XSRF-TOKEN";
    public static final String HEADER_X_REQUEST_ID = "X-REQUEST-ID";

    // Actual values are set in feign request interceptor (FeignConfig.java)
    public static final String COMPUTE_X_TIMESTAMP_UTC = null;
    public static final String COMPUTE_X_REQUEST_SIGNATURE = null;
    public static final String COMPUTE_FINTECH_ID = null;
}
