package de.adorsys.opba.tppbankingapi.controller;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Will be removed when Service-Session-Password will be removed from API.
 */
@Deprecated
@UtilityClass
public class PasswordExtractingUtil {

    @NotNull
    public String getDataProtectionPassword(String serviceSessionPassword, String fintechDataPassword) {
        var dataPassword = Strings.isBlank(serviceSessionPassword) ? fintechDataPassword : serviceSessionPassword;
        if (Strings.isBlank(dataPassword)) {
            throw new MissingDataProtectionPassword();
        }
        return dataPassword;
    }
}
