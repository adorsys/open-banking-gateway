package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedPaymentParameters;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aAuthorizedPaymentParametersLog extends Xs2aAuthorizedPaymentParameters implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aAuthorizedPaymentParametersLog("
                + ")";
    }
}
