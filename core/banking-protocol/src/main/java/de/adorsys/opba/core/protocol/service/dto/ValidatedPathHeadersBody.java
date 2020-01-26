package de.adorsys.opba.core.protocol.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ValidatedPathHeadersBody<P, H, B> {

    private final P path;
    private final H headers;
    private final B body;
}
