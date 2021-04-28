package de.adorsys.opba.protocol.xs2a.util.logresolver.domain;

import lombok.Data;


@Data
public class PathQueryHeadersParametersLog {

    private String resourceId;
    private String consentId;
    private String psuId;
    private String aspspId;
    private String requestId;

    public String getNotSensitiveData() {
        return "PathQueryHeadersLog("
                + "resourceId=" + getResourceId()
                + ", requestId=" + getRequestId()
                + ")";
    }
}
