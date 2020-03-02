package de.adorsys.opba.fintech.impl.tppclients;

import org.springframework.http.HttpHeaders;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class HeaderFields {
    public static final String AUTHORIZATION_SESSION_ID = "Authorization-Session-ID";
    public static final String PSU_CONSENT_SESSION = "PSU-Consent-Session";
    public static final String REDIRECT_CODE = "Redirect-Code";
    public static final String X_REQUEST_ID = "X-Request-ID";
    public static final String ALLOW = "Access-Control-Allow-Origin";

}
