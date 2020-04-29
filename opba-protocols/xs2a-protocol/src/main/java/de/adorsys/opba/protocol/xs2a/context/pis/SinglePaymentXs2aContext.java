package de.adorsys.opba.protocol.xs2a.context.pis;

import de.adorsys.opba.protocol.api.dto.payment.PaymentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * XS2A context for payment initiation. Represents general knowledge about currently executed request, for example, contains
 * outcome results from previous requests as well as user input.
 */
// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SinglePaymentXs2aContext extends Xs2aPisContext {

    public SinglePaymentXs2aContext() {
        super.setPaymentType(PaymentType.SINGLE);
    }
}
