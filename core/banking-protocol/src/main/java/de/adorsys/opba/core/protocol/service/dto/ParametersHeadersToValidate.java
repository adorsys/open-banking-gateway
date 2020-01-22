package de.adorsys.opba.core.protocol.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@Data
@RequiredArgsConstructor
public class ParametersHeadersToValidate<P, H> {

    @Valid
    private final P parameters;

    @Valid
    private final H headers;
}
