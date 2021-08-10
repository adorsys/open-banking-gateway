package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aOauth2HeadersLog extends Xs2aStandardHeadersLog {

    @Override
    public String getNotSensitiveData() {
        return "Xs2aOauth2HeadersLog("
                + "requestId=" + this.getRequestId()
                + ")";
    }
}
