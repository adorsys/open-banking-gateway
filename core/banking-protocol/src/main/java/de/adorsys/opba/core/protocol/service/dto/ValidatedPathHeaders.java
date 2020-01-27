package de.adorsys.opba.core.protocol.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ValidatedPathHeaders<P, H> {

    private final P path;
    private final H headers;
}
