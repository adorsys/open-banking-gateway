package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;

/**
 * Structure that represents REST request that has validated Query parameter and Headers.
 * {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator} has validated this object
 * and it is ready to be used in ASPSP API call.
 * @param <Q> Query parameter object
 * @param <H> Headers object
 */
@Data
public class ValidatedQueryHeaders<Q, H> {

    private final Q query;
    private final H headers;
}
