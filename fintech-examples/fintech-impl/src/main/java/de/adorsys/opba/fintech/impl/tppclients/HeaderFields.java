package de.adorsys.opba.fintech.impl.tppclients;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class HeaderFields {
    public static final String TPP_AUTH_ID = "Authorization-Session-ID";
    public static final String FIN_TECH_AUTH_ID = "Auth-ID";
    public static final String FIN_TECH_REDIRECT_CODE = "Fintech-Redirect-Code";
    public static final String X_REQUEST_ID = "X-Request-ID";
    public static final String SERVICE_SESSION_ID = "Service-Session-ID";
    public static final String COMPUTE_PSU_IP_ADDRESS = "Compute-PSU-IP-Address";

    public static final String X_TIMESTAMP_UTC = "X-Timestamp-UTC";
    public static final String X_REQUEST_SIGNATURE = "X-Request-Signature";
    public static final String FINTECH_ID = "Fintech-ID";
}
