package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import de.adorsys.opba.protocol.api.dto.context.Context;
import lombok.Builder;
import lombok.ToString;


@ToString(callSuper = true)
@Builder
public class ServiceContextLog extends Context implements NotSensitiveData {

    @Override
    public String getNotSensitiveData() {
        return "BaseServiceContextLog("
                + "serviceSessionId=" + this.getServiceSessionId()
                + ", authSessionId=" + this.getAuthSessionId()
                + ", bankId=" + this.loggableBankId()
                + ", requestId=" + this.getServiceBankProtocolId()
                + ")";
    }
}
