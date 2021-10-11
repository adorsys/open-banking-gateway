package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithBalanceParameters;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aWithBalanceParametersLog extends Xs2aWithBalanceParameters implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aWithBalanceParametersLog("
                + ")";
    }
}
