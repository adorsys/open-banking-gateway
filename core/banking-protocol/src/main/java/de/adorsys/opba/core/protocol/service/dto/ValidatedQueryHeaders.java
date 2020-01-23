package de.adorsys.opba.core.protocol.service.dto;

import lombok.Data;

@Data
public class ValidatedQueryHeaders<Q, H> {

    private final Q query;
    private final H headers;
}
