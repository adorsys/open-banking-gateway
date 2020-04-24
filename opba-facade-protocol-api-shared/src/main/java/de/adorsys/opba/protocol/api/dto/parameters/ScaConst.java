package de.adorsys.opba.protocol.api.dto.parameters;

import lombok.experimental.UtilityClass;

/**
 * Constants used for {@link de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest}.
 */
// TODO: Make enum in API
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ScaConst {

    /**
     * PSU password field name. (I.e. TAN/password used to login to ASPSP)
     */
    public static final String PSU_PASSWORD = "PSU_PASSWORD";

    /**
     * SCA challenge result field name. (I.e. SMS from 2FA containing secret code)
     */
    public static final String SCA_CHALLENGE_DATA = "SCA_CHALLENGE_DATA";

    /**
     * SCA challenge ID field name. (Used to select from multiple sca challenges)
     */
    public static final String SCA_CHALLENGE_ID = "SCA_CHALLENGE_ID";
}
