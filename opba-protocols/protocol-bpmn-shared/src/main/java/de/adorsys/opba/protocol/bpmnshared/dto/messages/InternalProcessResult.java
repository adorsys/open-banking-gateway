package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Class that represents some generic internal process result.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class InternalProcessResult {

    @NonNull
    private String processId;

    @NonNull
    private String executionId;

    private Object result;
}
