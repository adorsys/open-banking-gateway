package de.adorsys.opba.protocol.api.pis;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusRequest;

@FunctionalInterface
public interface GetPaymentStatusState extends Action<PaymentStatusRequest, PaymentStatusBody> {
}
