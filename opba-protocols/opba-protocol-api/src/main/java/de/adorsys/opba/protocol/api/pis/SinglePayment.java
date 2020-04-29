package de.adorsys.opba.protocol.api.pis;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.authorization.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;


@FunctionalInterface
public interface SinglePayment extends Action<InitiateSinglePaymentRequest, SinglePaymentBody> {
}
