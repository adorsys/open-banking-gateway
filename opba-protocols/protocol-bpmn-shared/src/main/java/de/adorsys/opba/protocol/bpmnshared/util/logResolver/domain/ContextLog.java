package de.adorsys.opba.protocol.bpmnshared.util.logResolver.domain;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode;
import lombok.Data;

import java.util.UUID;


@Data
public class ContextLog {

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

    public String getNotSensitiveData() {
        return "ContextLog("
                + "mode=" + this.getMode()
//                + ", aspspId=" + this.getAspspId()
                + ", action=" + this.getAction()
                + ", sagaId=" + this.getSagaId()
                + ", requestId=" + this.getRequestId()
                + ", serviceSessionId=" + this.getServiceSessionId()
//                + ", authorizationSessionIdIfOpened=" + this.getAuthorizationSessionIdIfOpened()
//                + ", redirectCodeIfAuthContinued=" + this.getRedirectCodeIfAuthContinued()
//                + ", aspspRedirectCode=" + this.getAspspRedirectCode()
//                + ", lastRedirectTo=" + this.getLastRedirectTo()
//                + ", lastRedirectToUiScreen=" + this.getLastRedirectToUiScreen()
//                + ", wrongAuthCredentials=" + this.getWrongAuthCredentials()
//                + ", selectedScaType=" + this.getSelectedScaType()
                + ")";
    }
}
