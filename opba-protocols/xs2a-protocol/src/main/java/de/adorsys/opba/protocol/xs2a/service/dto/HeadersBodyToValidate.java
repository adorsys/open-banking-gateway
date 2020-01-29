package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@Data
@RequiredArgsConstructor
public class HeadersBodyToValidate<H, B> {

    @Valid
    private final H headers;

    @Valid
    private final B body;
}
