package de.adorsys.opba.protocol.xs2a.service.dto;

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
