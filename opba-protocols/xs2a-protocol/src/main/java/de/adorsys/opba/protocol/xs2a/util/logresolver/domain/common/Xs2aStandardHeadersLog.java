package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.ToString;


@ToString(callSuper = true)
public class Xs2aStandardHeadersLog extends Xs2aStandardHeaders implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aStandardHeadersLog("
                + "requestId=" + this.getRequestId()
                + ")";
    }
}
