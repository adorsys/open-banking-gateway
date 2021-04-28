package de.adorsys.opba.protocol.hbci.util.logresolver.domain;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import lombok.ToString;

@ToString(callSuper = true)
public class BaseContextLog extends BaseContext {

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
