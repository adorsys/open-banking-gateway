package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import lombok.Data;


@Data
public class ConsentInitiateHeadersLog implements NotSensitiveData {

    private String psuId;
    private String aspspId;
    private String requestId;
    private String oauth2Token;

    private String psuIpAddress;
    private String redirectUriOk;
    private String redirectUriNok;
    private String psuIpPort;

    @Override
    public String getNotSensitiveData() {
        return "ConsentInitiateHeadersLog("
                + "requestId=" + this.getRequestId()
                + ")";
    }
}
