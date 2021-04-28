package de.adorsys.opba.protocol.hbci.util.logresolver.domain;

import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import lombok.ToString;


@ToString(callSuper = true)
public class TransactionListHbciContextLog extends TransactionListHbciContext {

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
