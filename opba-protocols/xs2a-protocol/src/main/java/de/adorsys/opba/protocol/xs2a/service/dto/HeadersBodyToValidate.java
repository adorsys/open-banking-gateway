package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

/**
 * Structure that represents REST request that has Headers and Body.
 * {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator} will validate the resulting object
 * in order to check if all necessary parameters to call ASPSP API are present.
 * @param <H> Headers object
 * @param <B> Body object
 */
@Data
@RequiredArgsConstructor
public class HeadersBodyToValidate<H, B> {

    @Valid
    private final H headers;

    @Valid
    private final B body;
}
