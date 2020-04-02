package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@Data
@RequiredArgsConstructor
public class PathHeadersBodyToValidate<P, H, B> {

    @Valid
    private final P path;

    @Valid
    private final H headers;

    @Valid
    private final B body;
}
