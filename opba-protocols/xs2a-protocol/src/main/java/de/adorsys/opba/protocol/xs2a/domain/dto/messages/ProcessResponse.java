package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The event that is typically sent when data from ASPSP is acquired. For example - when account list was received.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ProcessResponse extends InternalProcessResult {

    @Builder
    public ProcessResponse(String processId, String executionId, Object result) {
        super(processId, executionId, result);
    }
}
