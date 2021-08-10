package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2WithCodeParameters;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aOauth2WithCodeParametersLog extends Xs2aOauth2WithCodeParameters implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aOauth2WithCodeParametersLog("
                + ")";
    }
}
