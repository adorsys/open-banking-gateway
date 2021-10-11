package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class BaseContextLog extends BaseContext implements NotSensitiveData {

    @Override
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
