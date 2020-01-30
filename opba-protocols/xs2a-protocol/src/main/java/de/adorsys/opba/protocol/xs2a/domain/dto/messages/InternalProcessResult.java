package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class InternalProcessResult {

    private String processId;
    private Object result;
}
