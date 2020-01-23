package de.adorsys.opba.core.protocol.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@Data
@RequiredArgsConstructor
public class PathHeadersToValidate<P, H> {

    @Valid
    private final P path;

    @Valid
    private final H headers;
}
