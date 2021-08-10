package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;


@Data
public class SelectPsuAuthenticationMethodLog implements NotSensitiveData {

    private String authenticationMethodId;

    @Override
    public String getNotSensitiveData() {
        return "SelectPsuAuthenticationMethodLog("
                + ")";
    }
}
