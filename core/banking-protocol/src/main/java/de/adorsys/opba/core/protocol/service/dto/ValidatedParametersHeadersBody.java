package de.adorsys.opba.core.protocol.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ValidatedParametersHeadersBody<P, H, B> {

    private final P parameters;
    private final H headers;
    private final B body;
}
