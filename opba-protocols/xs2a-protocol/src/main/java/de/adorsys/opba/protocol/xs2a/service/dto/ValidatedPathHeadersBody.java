package de.adorsys.opba.protocol.xs2a.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Structure that represents REST request that has validated Path parameter, Headers and Body.
 * {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator} has validated this object
 * and it is ready to be used in ASPSP API call.
 * @param <P> Path parameter object
 * @param <H> Headers object
 * @param <B> Body object
 */
@Data
@RequiredArgsConstructor
public class ValidatedPathHeadersBody<P, H, B> {

    private final P path;
    private final H headers;
    private final B body;
}
