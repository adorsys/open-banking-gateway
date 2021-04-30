package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.response.TokenResponseLog;
import de.adorsys.xs2a.adapter.api.model.StartScaprocessResponse;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString(callSuper = true)
public class Xs2aContextLog extends BaseContextLog {

    private String psuId;
    private String psuIpAddress;
    private String aspspScaApproach;
    private StartScaprocessResponse startScaProcessResponse;
    private String consentId;
    private String authorizationId;
    private String scaStatus;
    private List<ScaMethod> availableSca;
    private String userSelectScaId;
    private AuthenticationObjectLog scaSelected;
    private String redirectUriOk;
    private String redirectUriNok;
    private String fintechRedirectUriOk;
    private String fintechRedirectUriNok;

    private boolean redirectConsentOk;
    private String psuIpPort;
    private ChallengeDataLog challengeData;
    private String oauth2Code;
    private TokenResponseLog oauth2Token;
    private boolean oauth2PreStepNeeded;
    private boolean oauth2IntegratedNeeded;
    private String scaOauth2Link;
    private String oauth2RedirectBackLink;

    @Override
    public String getNotSensitiveData() {
        return "Xs2aContextLog("
                + "mode=" + this.getMode()
                + ", action=" + this.getAction()
                + ", sagaId=" + this.getSagaId()
                + ", requestId=" + this.getRequestId()
                + ", serviceSessionId=" + this.getServiceSessionId()
                + ")";
    }
}
