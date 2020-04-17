package de.adorsys.opba.fintech.impl.tppclients;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class HeaderFields {
    public static final String AUTHORIZATION_SESSION_ID = "Authorization-Session-ID";
    public static final String PSU_CONSENT_SESSION = "PSU-Consent-Session";
    public static final String REDIRECT_CODE = "Redirect-Code";
    public static final String X_REQUEST_ID = "X-Request-ID";
    public static final String SERVICE_SESSION_ID = "Service-Session-ID";
    public static final String COMPUTE_PSU_IP_ADDRESS = "Compute-PSU-IP-Address";

    public static final String X_TIMESTAMP_UTC = "X-Timestamp-UTC";
    public static final String X_REQUEST_SIGNATURE = "X-Request-Signature";
    public static final String FINTECH_ID = "fintech_id"; // todo delete AUTHORISATION header
}
