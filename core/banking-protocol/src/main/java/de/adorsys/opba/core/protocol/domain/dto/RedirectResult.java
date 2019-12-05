package de.adorsys.opba.core.protocol.domain.dto;

import lombok.Data;

@Data
public class RedirectResult extends ProcessResult {

    private String redirectUri;
}
