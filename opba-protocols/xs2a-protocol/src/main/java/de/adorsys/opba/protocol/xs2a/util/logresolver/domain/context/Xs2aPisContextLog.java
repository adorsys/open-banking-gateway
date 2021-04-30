package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import de.adorsys.opba.protocol.api.dto.payment.PaymentType;
import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class Xs2aPisContextLog extends Xs2aContextLog {

    private String paymentId;
    private PaymentType paymentType;
    private String paymentProduct;
    private boolean isAuthorized;

}
