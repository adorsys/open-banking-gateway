package de.adorsys.opba.core.protocol.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@Data
@RequiredArgsConstructor
public class ParametersHeadersBodyToValidate<P, H, B> {

    @Valid
    private final P parameters;

    @Valid
    private final H headers;

    @Valid
    private final B body;
}
