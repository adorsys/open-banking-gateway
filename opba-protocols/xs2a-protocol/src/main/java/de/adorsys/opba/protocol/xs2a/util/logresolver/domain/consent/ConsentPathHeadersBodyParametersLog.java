package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import lombok.Data;


@Data
public class ConsentPathHeadersBodyParametersLog {

    private String consentId;
    private String authorizationId;
    private String psuId;
    private String aspspId;
    private String requestId;
    private Boolean tppRedirectPreferred;
    private String oauth2Token;
    private String authenticationMethodId;

    public String getNotSensitiveData() {
        return "PathHeadersBodyParametersLog("
                + ", requestId=" + this.getRequestId()
                + ")";
    }
}
