package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class SinglePaymentInitiationServiceProvider {

    private final Set<SinglePaymentInitiationService> paymentInitiationServices;

    public SinglePaymentInitiationService instance(Xs2aPisContext context) {
        return paymentInitiationServices.stream()
                .filter(it -> it.isXs2aApiVersionSupported(context.aspspProfile().getSupportedXs2aApiVersion()))
                .findAny().orElseThrow();
    }
}
