package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;

@Data
public class ValidatedQueryHeaders<Q, H> {

    private final Q query;
    private final H headers;
}
