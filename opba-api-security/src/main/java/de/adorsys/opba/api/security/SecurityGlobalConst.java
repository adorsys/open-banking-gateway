package de.adorsys.opba.api.security;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class SecurityGlobalConst {

    public static final String DISABLED_SECURITY_OR_ENABLED_NO_SIGNATURE_FILTER_PROFILE = "no-signature-filter | security-bypass";
    public static final String DISABLED_SECURITY_PROFILE = "security-bypass";
    public static final String ENABLED_SECURITY_AND_DISABLED_NO_SIGNATURE_FILTER_PROFILE = "!no-signature-filter & !security-bypass";
    public static final String ENABLED_SECURITY_PROFILE = "!security-bypass";
}
