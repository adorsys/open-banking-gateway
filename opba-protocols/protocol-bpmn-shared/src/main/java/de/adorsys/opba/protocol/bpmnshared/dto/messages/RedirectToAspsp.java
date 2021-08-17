package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The event that is typically sent just before redirect to ASPSP.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RedirectToAspsp extends Redirect {

    public RedirectToAspsp(Redirect redirect) {
        super(redirect.getProcessId(), redirect.getExecutionId(), redirect.getResult(), redirect.getRedirectUri());
    }
}
