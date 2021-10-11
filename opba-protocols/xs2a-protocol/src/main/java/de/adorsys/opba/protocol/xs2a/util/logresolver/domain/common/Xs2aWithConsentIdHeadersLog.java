package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aWithConsentIdHeadersLog extends Xs2aStandardHeadersLog {

    private String consentId;

    @Override
    public String getNotSensitiveData() {
        return "Xs2aWithConsentIdHeadersLog("
                + "requestId=" + this.getRequestId()
                + ")";
    }
}
