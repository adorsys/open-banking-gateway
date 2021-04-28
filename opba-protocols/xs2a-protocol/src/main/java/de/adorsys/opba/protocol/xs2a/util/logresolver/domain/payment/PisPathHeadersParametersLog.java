package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment;

import de.adorsys.opba.protocol.api.dto.payment.PaymentType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class PisPathHeadersParametersLog {

    private String paymentId;
    private PaymentType paymentType;
    private String paymentProduct;
    private String psuId;
    private String aspspId;
    private String requestId;
    private Boolean tppRedirectPreferred;
    private String oauth2Token;

    public String getNotSensitiveData() {
        return "PathHeadersBodyParametersLog("
                + ", requestId=" + this.getRequestId()
                + ")";
    }
}
