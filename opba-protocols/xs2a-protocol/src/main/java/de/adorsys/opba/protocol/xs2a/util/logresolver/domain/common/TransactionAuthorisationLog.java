package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;


@Data
public class TransactionAuthorisationLog implements NotSensitiveData {

    private String scaAuthenticationData;

    @Override
    public String getNotSensitiveData() {
        return "TransactionAuthorisationLog("
                + ")";
    }
}
