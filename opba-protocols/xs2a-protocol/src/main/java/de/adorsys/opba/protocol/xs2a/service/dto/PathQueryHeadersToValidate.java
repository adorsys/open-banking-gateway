package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@Data
@RequiredArgsConstructor
public class PathQueryHeadersToValidate<P, Q, H> {

    @Valid
    private final P path;

    @Valid
    private final Q query;

    @Valid
    private final H headers;
}
