package de.adorsys.opba.protocol.facade.config.auth;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class UriExpandConst {

    public static final String FINTECH_USER_TEMP_PASSWORD = "fintechUserTempPassword";
    public static final String AUTHORIZATION_SESSION_ID = "authorizationSessionId";
    public static final String REDIRECT_STATE = "redirectState";
}
