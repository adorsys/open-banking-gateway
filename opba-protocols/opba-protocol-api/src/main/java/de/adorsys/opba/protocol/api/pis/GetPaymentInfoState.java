package de.adorsys.opba.protocol.api.pis;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoRequest;

@FunctionalInterface
public interface GetPaymentInfoState extends Action<PaymentInfoRequest, PaymentInfoBody> {
}
