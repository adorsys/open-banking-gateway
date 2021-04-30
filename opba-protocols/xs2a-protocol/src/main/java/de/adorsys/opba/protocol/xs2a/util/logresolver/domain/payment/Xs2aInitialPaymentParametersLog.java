package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment;

import de.adorsys.opba.protocol.api.dto.payment.PaymentType;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import lombok.Data;


@Data
public class Xs2aInitialPaymentParametersLog implements NotSensitiveData {

    private PaymentType paymentType;
    private PaymentProduct paymentProduct;

    @Override
    public String getNotSensitiveData() {
        return "Xs2aInitialPaymentParametersLog("
                + ")";
    }
}
