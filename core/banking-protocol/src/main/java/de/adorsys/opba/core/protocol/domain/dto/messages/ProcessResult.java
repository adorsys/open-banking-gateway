package de.adorsys.opba.core.protocol.domain.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResult {

    private String processId;
    private Object result;
}
