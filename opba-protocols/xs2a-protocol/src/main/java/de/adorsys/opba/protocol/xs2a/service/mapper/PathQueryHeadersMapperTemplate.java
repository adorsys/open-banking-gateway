package de.adorsys.opba.protocol.xs2a.service.mapper;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.dto.PathQueryHeadersToValidate;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathQueryHeaders;
import lombok.RequiredArgsConstructor;

/**
 * Mapper class to map from context object like {@link de.adorsys.opba.protocol.xs2a.context.Xs2aContext}
 * to ASPSP API request parameters (path parameters, query parameters, headers).
 *
 * @param <C> Context class
 * @param <P> ASPSP API path parameters class
 * @param <Q> ASPSP API query parameters class
 * @param <H> ASPSP API headers class
 */
@RequiredArgsConstructor
public class PathQueryHeadersMapperTemplate<C extends BaseContext, P, Q, H> {

    private final DtoMapper<? super C, H> toHeaders;
    private final DtoMapper<? super C, P> toPath;
    private final DtoMapper<? super C, Q> toQuery;

    /**
     * Converts context object into object that can be used for validation.
     *
     * @param context Context to convert
     * @return Validatable object that can be used with {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator}
     * to check if all necessary parameters are present
     */
    public PathQueryHeadersToValidate<P, Q, H> forValidation(C context) {
        return new PathQueryHeadersToValidate<>(
                toPath.map(context),
                toQuery.map(context),
                toHeaders.map(context)
        );
    }

    /**
     * Converts context object into object that can be used for ASPSP API call.
     *
     * @param context Context to convert
     * @return Object that can be used with {@code Xs2aAdapter} to perform ASPSP API calls
     */
    public ValidatedPathQueryHeaders<P, Q, H> forExecution(C context) {
        return new ValidatedPathQueryHeaders<>(
                toPath.map(context),
                toQuery.map(context),
                toHeaders.map(context)
        );
    }
}
