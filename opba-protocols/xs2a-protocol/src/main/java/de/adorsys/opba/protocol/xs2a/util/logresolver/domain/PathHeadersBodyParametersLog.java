package de.adorsys.opba.protocol.xs2a.util.logresolver.domain;

import lombok.Data;
import java.time.LocalDate;


@Data
public class PathHeadersBodyParametersLog {

    private String psuId;
    private String aspspId;
    private String requestId;
    private String oauth2Token;
    private String psuIpAddress;
    private String redirectUriOk;
    private String redirectUriNok;
    private String psuIpPort;
    private AccountAccessLog access;
    private Boolean recurringIndicator;
    private LocalDate validUntil;
    private Integer frequencyPerDay;
    private Boolean combinedServiceIndicator;

    public String getNotSensitiveData() {
        return "PathHeadersBodyParametersLog("
//                + "psuId=" + this.getPsuId()
//                + ", aspspId=" + this.getAspspId()
                + ", requestId=" + this.getRequestId()
//                + ", oauth2Token=" + this.getOauth2Token()
//                + ", psuIpAddress=" + this.getPsuIpAddress()
//                + ", redirectUriOk=" + this.getRedirectUriOk()
//                + ", redirectUriNok=" + this.getRedirectUriNok()
//                + ", psuIpPort=" + this.getPsuIpPort()
//                + ", access=" + this.getAccess()
//                + ", recurringIndicator=" + this.getRecurringIndicator()
//                + ", validUntil=" + this.getValidUntil()
//                + ", frequencyPerDay=" + this.getFrequencyPerDay()
//                + ", combinedServiceIndicator=" + this.getCombinedServiceIndicator()
                + ")";

    }
}
