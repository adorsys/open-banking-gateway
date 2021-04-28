package de.adorsys.opba.protocol.hbci.util.logresolver.domain;

import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.ToString;

@ToString(callSuper = true)
public class HbciContextLog extends HbciContext {

    public String getNotSensitiveData() {
        return "ContextLog("
                + "mode=" + this.getMode()
                + ", action=" + this.getAction()
                + ", sagaId=" + this.getSagaId()
                + ", requestId=" + this.getRequestId()
                + ", serviceSessionId=" + this.getServiceSessionId()
                + ")";
    }
}
