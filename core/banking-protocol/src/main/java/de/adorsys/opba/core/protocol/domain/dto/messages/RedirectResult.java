package de.adorsys.opba.core.protocol.domain.dto.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RedirectResult extends ProcessResult {

    private String redirectUri;
}
