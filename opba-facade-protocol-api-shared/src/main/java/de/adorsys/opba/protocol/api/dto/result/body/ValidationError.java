package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

@Data
public class ValidationError {

    private String type;
    private String scope;
    private String code;
    private String captionMessage;
}
