package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStartPaymentAuthorizationParameters;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import lombok.ToString;


@ToString(callSuper = true)
public class Xs2aStartPaymentAuthorizationParametersLog extends Xs2aStartPaymentAuthorizationParameters implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aStartPaymentAuthorizationParametersLog("
                + ")";
    }
}
