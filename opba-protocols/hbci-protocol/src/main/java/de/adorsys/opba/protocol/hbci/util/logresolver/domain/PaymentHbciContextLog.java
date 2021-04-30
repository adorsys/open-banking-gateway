package de.adorsys.opba.protocol.hbci.util.logresolver.domain;

import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PaymentInitiateBody;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PisSinglePaymentResult;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class PaymentHbciContextLog extends HbciContextLog {

    private String accountIban;
    private PaymentInitiateBody payment;
    private PisSinglePaymentResult response;

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
