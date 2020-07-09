package de.adorsys.opba.protocol.hbci.context;

import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PaymentInitiateBody;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PisSinglePaymentResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentHbciContext extends HbciContext {

    /**
     * Account IBAN to make payment from.
     */
    private String accountIban;

    /**
     * Body for a payment initiation request message.
     */
    private PaymentInitiateBody payment;

    /**
     * Real-time result of the operation as HBCI protocol does not have support for consent.
     */
    private PisSinglePaymentResult response;
}
