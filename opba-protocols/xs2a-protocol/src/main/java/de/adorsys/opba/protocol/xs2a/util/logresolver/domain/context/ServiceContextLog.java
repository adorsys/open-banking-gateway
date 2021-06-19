package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import de.adorsys.opba.protocol.api.dto.context.Context;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Delegate;

@Data
@ToString(callSuper = true)
public class ServiceContextLog implements NotSensitiveData {

    @Delegate
    private Context context;

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

