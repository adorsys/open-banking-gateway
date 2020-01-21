package de.adorsys.opba.tppbankingapi;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") //Checkstyle doesn't recognise Lombok
public class HttpHeaders {
    public static final String PSU_CONSENT_SESSION = "PSU-Consent-Session";
    public static final String X_REQUEST_ID = "X-Request-ID";
}
