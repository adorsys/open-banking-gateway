package de.adorsys.opba.protocol.hbci.service.protocol;

import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.PaymentResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.transaction.AbstractPayment;

public interface CustomizedOnlineBankingService extends OnlineBankingService {
    PaymentResponse executePayment(TransactionRequest<? extends AbstractPayment> paymentRequest, String end2EndId);
}
