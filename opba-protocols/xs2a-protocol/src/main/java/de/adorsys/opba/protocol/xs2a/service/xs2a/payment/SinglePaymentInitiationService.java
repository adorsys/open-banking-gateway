package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.PaymentInitationRequestResponse201;
import org.flowable.engine.delegate.DelegateExecution;

public interface SinglePaymentInitiationService {

     void doValidate(DelegateExecution execution, Xs2aPisContext context);

     Response<PaymentInitationRequestResponse201> doExecution(DelegateExecution execution, Xs2aPisContext context);

     boolean isXs2aApiVersionSupported(String apiVersion);

}
