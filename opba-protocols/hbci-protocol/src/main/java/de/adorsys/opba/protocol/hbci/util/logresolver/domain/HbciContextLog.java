package de.adorsys.opba.protocol.hbci.util.logresolver.domain;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.ChallengeData;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.multibanking.hbci.model.HbciTanSubmit;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString(callSuper = true)
public class HbciContextLog extends BaseContextLog {

    private String psuId;
    private String fintechRedirectUriOk;
    private String fintechRedirectUriNok;
    private Bank bank;
    private HbciConsent hbciDialogConsent;
    private HbciTanSubmit hbciTanSubmit;
    private boolean tanChallengeRequired;
    private ChallengeData challengeData;
    private List<ScaMethod> availableSca;
    private String userSelectScaId;
    private boolean consentIncompatible;
    private String psuTan;
    private String hbciPassportState;
    private HbciResultCache cachedResult;
    private Boolean online;

    @Override
    public String getNotSensitiveData() {
        return "HbciContextLog("
                + "mode=" + this.getMode()
                + ", action=" + this.getAction()
                + ", sagaId=" + this.getSagaId()
                + ", requestId=" + this.getRequestId()
                + ", serviceSessionId=" + this.getServiceSessionId()
                + ")";
    }
}
