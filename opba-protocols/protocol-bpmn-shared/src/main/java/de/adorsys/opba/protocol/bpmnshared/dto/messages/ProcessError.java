package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * General response that represents that unrecoverable error happened.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ProcessError extends InternalProcessResult {

    private final String message;
    private final boolean canRedirectBackToFintech;

    @Builder(toBuilder = true)
    public ProcessError(String processId, String executionId, String message, boolean canRedirectBackToFintech) {
        super(processId, executionId, null);
        this.message = message;
        this.canRedirectBackToFintech = false;
    }
}
