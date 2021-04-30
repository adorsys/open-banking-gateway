package de.adorsys.opba.protocol.hbci.util.logresolver.domain;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class BaseContextLog extends BaseContext {

    public String getNotSensitiveData() {
        return "BaseContextLog("
                + "mode=" + this.getMode()
                + ", action=" + this.getAction()
                + ", sagaId=" + this.getSagaId()
                + ", requestId=" + this.getRequestId()
                + ", serviceSessionId=" + this.getServiceSessionId()
                + ")";
    }
}
