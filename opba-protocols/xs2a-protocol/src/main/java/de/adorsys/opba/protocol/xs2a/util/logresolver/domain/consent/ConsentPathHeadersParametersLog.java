package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import lombok.Data;


@Data
public class ConsentPathHeadersParametersLog {

    private String consentId;
    private String psuId;
    private String aspspId;
    private String requestId;
    private Boolean tppRedirectPreferred;
    private String oauth2Token;

    public String getNotSensitiveData() {
        return "PathHeadersBodyParametersLog("
                + ", requestId=" + this.getRequestId()
                + ")";
    }
}
