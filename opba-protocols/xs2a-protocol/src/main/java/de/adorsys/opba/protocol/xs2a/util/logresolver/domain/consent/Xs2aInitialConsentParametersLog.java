package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialConsentParameters;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import lombok.ToString;


@ToString(callSuper = true)
public class Xs2aInitialConsentParametersLog extends Xs2aInitialConsentParameters implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aInitialConsentParametersLog("
                + ")";
    }
}
