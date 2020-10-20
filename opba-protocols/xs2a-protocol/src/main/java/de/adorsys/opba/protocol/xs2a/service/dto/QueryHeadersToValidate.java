package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;

/**
 * Structure that represents REST request that Query parameter and Headers.
 * {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator} will validate the resulting object
 * in order to check if all necessary parameters to call ASPSP API are present.
 *
 * @param <Q> Query parameter object.
 * @param <H> Headers object
 */
@Data
@AllArgsConstructor
public class QueryHeadersToValidate<Q, H> {

    @Valid
    private final Q query;

    @Valid
    private final H headers;
}
