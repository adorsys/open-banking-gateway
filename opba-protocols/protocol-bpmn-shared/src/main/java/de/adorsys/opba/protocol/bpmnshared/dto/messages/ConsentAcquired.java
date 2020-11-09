package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The event that is typically sent when consent is acquired and user should be redirected back to FinTech.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ConsentAcquired extends Redirect {

    public ConsentAcquired(Redirect redirect) {
        super(redirect.getProcessId(), redirect.getExecutionId(), redirect.getResult(), redirect.getRedirectUri(), false);
    }
}
