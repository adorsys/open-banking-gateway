package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RedirectResult extends ProcessResult {

    private String redirectUri;
}
