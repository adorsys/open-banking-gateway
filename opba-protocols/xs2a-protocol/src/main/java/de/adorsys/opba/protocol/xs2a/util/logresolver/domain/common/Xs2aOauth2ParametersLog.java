package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Parameters;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aOauth2ParametersLog extends Xs2aOauth2Parameters implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aOauth2ParametersLog("
                + ")";
    }
}
