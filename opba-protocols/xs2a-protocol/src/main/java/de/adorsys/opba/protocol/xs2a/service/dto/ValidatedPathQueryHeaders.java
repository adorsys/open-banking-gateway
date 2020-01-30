package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ValidatedPathQueryHeaders<P, Q, H> {

    private final P path;
    private final Q query;
    private final H headers;
}
