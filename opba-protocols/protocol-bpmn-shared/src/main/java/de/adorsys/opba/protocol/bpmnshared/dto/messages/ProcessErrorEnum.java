package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProcessErrorEnum {
    CONSENT_UNKNOWN(398),
    CONSENT_ACCESS_EXCEEDED_LIMIT(399);

    private final int code;
}
