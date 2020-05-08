package de.adorsys.opba.protocol.xs2a.context.pis;

import de.adorsys.opba.protocol.api.dto.payment.PaymentType;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateBody;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * XS2A PIS (Payment Initiation Service) context. Represents general knowledge about currently executed request,
 * for example, contains outcome results from previous requests as well as the user input.
 */
// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aPisContext extends Xs2aContext {

    /**
     * Payment ID that uniquely identifies the payment within ASPSP. Highly sensitive field.
     */
    private String paymentId;

    /**
     * Payment service is provided by ASPSP.
     */
    private PaymentType paymentType;

    /**
     * Payment product is provided by ASPSP.
     */
    private String paymentProduct;

    /**
     * Body for a payment initiation request message.
     */
    private PaymentInitiateBody payment = new PaymentInitiateBody();
}
