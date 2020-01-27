package de.adorsys.opba.core.protocol.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;

@Data
@AllArgsConstructor
public class QueryHeadersToValidate<Q, H> {

    @Valid
    private final Q query;

    @Valid
    private final H headers;
}
