package de.adorsys.opba.core.protocol.domain.dto;

import lombok.Data;

@Data
public class ProcessResult {

    private String processId;
    private Object result;
}
