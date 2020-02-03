package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

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
