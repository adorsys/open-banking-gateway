package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment;

import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.ConsentInitiateHeadersLog;
import lombok.ToString;


@ToString(callSuper = true)
public class PaymentInitiateHeadersLog extends ConsentInitiateHeadersLog {

    @Override
    public String getNotSensitiveData() {
        return "PaymentInitiateHeadersLog("
                + "requestId=" + this.getRequestId()
                + ")";
    }
}
