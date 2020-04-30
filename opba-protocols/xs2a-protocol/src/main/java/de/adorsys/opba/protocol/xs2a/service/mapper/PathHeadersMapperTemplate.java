package de.adorsys.opba.protocol.xs2a.service.mapper;

import de.adorsys.opba.protocol.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.dto.PathHeadersToValidate;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

/**
 * Mapper class to map from context object like {@link de.adorsys.opba.protocol.xs2a.context.Xs2aContext}
 * to ASPSP API request parameters (path parameters, headers).
 * @param <C> Context class
 * @param <P> ASPSP API path parameters class
 * @param <H> ASPSP API headers class
 */
@RequiredArgsConstructor
public class PathHeadersMapperTemplate<C extends BaseContext, P, H> {

    private final DtoMapper<? super C, H> toHeaders;
    private final DtoMapper<? super C, P> toPath;

    /**
     * Converts context object into object that can be used for validation.
     * @param context Context to convert
     * @return Validatable object that can be used with {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator}
     * to check if all necessary parameters are present
     */
    public PathHeadersToValidate<P, H> forValidation(C context) {
        return new PathHeadersToValidate<>(
                toPath.map(context),
                toHeaders.map(context)
        );
    }

    /**
     * Converts context object into object that can be used for ASPSP API call.
     * @param context Context to convert
     * @return Object that can be used with {@code Xs2aAdapter} to perform ASPSP API calls
     */
    public ValidatedPathHeaders<P, H> forExecution(C context) {
        return new ValidatedPathHeaders<>(
                toPath.map(context),
                toHeaders.map(context)
        );
    }
}
