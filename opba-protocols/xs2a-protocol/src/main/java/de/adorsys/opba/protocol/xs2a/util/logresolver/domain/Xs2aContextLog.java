package de.adorsys.opba.protocol.xs2a.util.logresolver.domain;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.xs2a.adapter.api.model.AuthenticationObject;
import de.adorsys.xs2a.adapter.api.model.ChallengeData;
import de.adorsys.xs2a.adapter.api.model.TokenResponse;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class Xs2aContextLog {
    private ContextMode mode;
    private String aspspId;
    private ProtocolAction action;
    private String sagaId;
    private String requestId;
    private UUID serviceSessionId;
    private String authorizationSessionIdIfOpened;
    private String redirectCodeIfAuthContinued;
    private String aspspRedirectCode;
    private String lastRedirectTo;
    private String lastRedirectToUiScreen;
    private Boolean wrongAuthCredentials;
    private String selectedScaType;
    private String psuId;
    private String psuIpAddress;
    private String aspspScaApproach;
    private StartScaprocessResponseLog startScaProcessResponse;
    private String consentId;
    private String authorizationId;
    private String scaStatus;
    private List<ScaMethod> availableSca;
    private String userSelectScaId;
    private AuthenticationObject scaSelected;
    private String redirectUriOk;
    private String redirectUriNok;
    private String fintechRedirectUriOk;
    private String fintechRedirectUriNok;
    private boolean redirectConsentOk;
    private String psuIpPort;
    private ChallengeData challengeData;
    private String oauth2Code;
    private TokenResponse oauth2Token;
    private boolean oauth2PreStepNeeded;
    private boolean oauth2IntegratedNeeded;
    private String scaOauth2Link;
    private String oauth2RedirectBackLink;

    public String getNotSensitiveData() {
        return "ContextLog("
                + ", sagaId=" + getSagaId()
                + ", requestId=" + getRequestId()
                + ", serviceSessionId=" + getServiceSessionId() + ")";
    }
}
