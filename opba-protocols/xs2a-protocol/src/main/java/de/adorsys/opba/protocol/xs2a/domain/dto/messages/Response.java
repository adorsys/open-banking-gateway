package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class Response extends InternalProcessResult {

    @Builder
    public Response(String processId, String executionId, Object result) {
        super(processId, executionId, result);
    }
}
