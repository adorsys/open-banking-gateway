package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The event that is typically sent when payment is acquired and user should be redirected back to FinTech.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class PaymentAcquired extends Redirect {

    public PaymentAcquired(Redirect redirect) {
        super(redirect.getProcessId(), redirect.getExecutionId(), redirect.getResult(), redirect.getRedirectUri(), false);
    }
}
