package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

/**
 * Structure that represents REST request that has Path parameter, Query parameter and Headers.
 * {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator} will validate the resulting object
 * in order to check if all necessary parameters to call ASPSP API are present.
 * @param <P> Path parameter object.
 * @param <Q> Query parameter object.
 * @param <H> Headers object
 */
@Data
@RequiredArgsConstructor
public class PathQueryHeadersToValidate<P, Q, H> {

    @Valid
    private final P path;

    @Valid
    private final Q query;

    @Valid
    private final H headers;
}
