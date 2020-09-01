package de.adorsys.opba.protocol.hbci.service.protocol.pis.dto;

import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class PaymentInitiateBodyWithPayment extends PaymentInitiateBody {

    @Delegate
    private PaymentInitiateBody paymentInitiateBody;

    private ProtocolFacingPayment payment;
}
