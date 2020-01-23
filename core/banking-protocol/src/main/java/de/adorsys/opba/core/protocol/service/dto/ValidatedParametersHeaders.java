package de.adorsys.opba.core.protocol.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ValidatedParametersHeaders<P, H> {

    private final P parameters;
    private final H headers;
}
