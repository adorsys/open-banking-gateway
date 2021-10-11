package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;


@Data
public class UpdatePsuAuthenticationLog implements NotSensitiveData {

    private PsuDataLog psuData;

    @Override
    public String getNotSensitiveData() {
        return "UpdatePsuAuthenticationLog("
                + ")";
    }
}
