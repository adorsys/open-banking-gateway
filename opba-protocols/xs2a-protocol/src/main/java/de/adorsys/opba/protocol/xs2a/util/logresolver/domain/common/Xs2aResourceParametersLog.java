package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aResourceParameters;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aResourceParametersLog extends Xs2aResourceParameters implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aResourceParametersLog("
                + ")";
    }
}
