package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ConsentAcquired extends Redirect {

    public ConsentAcquired(Redirect redirect) {
        super(redirect.getProcessId(), redirect.getExecutionId(), redirect.getResult(), redirect.getRedirectUri());
    }
}
