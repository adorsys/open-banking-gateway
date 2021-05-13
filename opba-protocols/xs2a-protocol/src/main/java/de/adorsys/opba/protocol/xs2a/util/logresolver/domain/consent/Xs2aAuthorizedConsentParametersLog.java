package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aAuthorizedConsentParametersLog extends Xs2aAuthorizedConsentParameters implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aAuthorizedConsentParametersLog("
                + ")";
    }
}
