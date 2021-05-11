package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.ToString;


@ToString
public class ConsentInitiateParametersLog implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "ConsentInitiateParametersLog("
                + ")";
    }
}
