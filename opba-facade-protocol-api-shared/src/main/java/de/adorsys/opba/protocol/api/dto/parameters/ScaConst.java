package de.adorsys.opba.protocol.api.dto.parameters;

import lombok.experimental.UtilityClass;

// TODO: Make enum in API
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ScaConst {

    public static final String PSU_PASSWORD = "psuPassword";
    public static final String SCA_CHALLENGE_DATA = "lastScaChallenge";
    public static final String SCA_CHALLENGE_ID = "userSelectScaId";
}
