package de.adorsys.opba.protocol.xs2a.service.mapper;

import de.adorsys.opba.protocol.xs2a.service.dto.QueryHeadersToValidate;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

/**
 * Mapper class to map from context object like {@link de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext}
 * to ASPSP API request parameters (query parameters, headers).
 * @param <C> Context class
 * @param <H> ASPSP API headers class
 * @param <Q> ASPSP API query params class
 */
@RequiredArgsConstructor
public class QueryHeadersMapperTemplate<C extends BaseContext, Q, H> {

    private final DtoMapper<? super C, H> toHeaders;
    private final DtoMapper<? super C, Q> toQuery;

    /**
     * Converts context object into object that can be used for validation.
     * @param context Context to convert
     * @return Validatable object that can be used with {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator}
     * to check if all necessary parameters are present
     */
    public QueryHeadersToValidate<Q, H> forValidation(C context) {
        return new QueryHeadersToValidate<>(
                toQuery.map(context),
                toHeaders.map(context)
        );
    }

    /**
     * Converts context object into object that can be used for ASPSP API call.
     * @param context Context to convert
     * @return Object that can be used with {@code Xs2aAdapter} to perform ASPSP API calls
     */
    public ValidatedQueryHeaders<Q, H> forExecution(C context) {
        return new ValidatedQueryHeaders<>(
                toQuery.map(context),
                toHeaders.map(context)
        );
    }
}
