package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"code", "scope"})
public class Cause {

    private String type;
    private String scope;
    private String code;
    private String captionMessage;
}
