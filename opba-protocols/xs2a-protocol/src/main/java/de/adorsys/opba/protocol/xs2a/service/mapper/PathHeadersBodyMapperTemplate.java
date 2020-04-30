package de.adorsys.opba.protocol.xs2a.service.mapper;

import de.adorsys.opba.protocol.xs2a.service.dto.PathHeadersBodyToValidate;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

/**
 * Mapper class to map from context object like {@link de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext}
 * to ASPSP API request parameters (path parameters, headers, body).
 * @param <C> Context class
 * @param <P> ASPSP API path parameters class
 * @param <H> ASPSP API headers class
 * @param <V> ASPSP API object to validate after mapping, compatible with {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator}
 * @param <B> ASPSP API object to use when doing API call
 */
@RequiredArgsConstructor
public class PathHeadersBodyMapperTemplate<C extends BaseContext, P, H, V, B> {

    private final DtoMapper<? super C, V> toValidatableBody;
    private final DtoMapper<V, B> toBody;
    private final DtoMapper<? super C, H> toHeaders;
    private final DtoMapper<? super C, P> toPath;

    /**
     * Converts context object into object that can be used for validation.
     * @param context Context to convert
     * @return Validatable object that can be used with {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator}
     * to check if all necessary parameters are present
     */
    public PathHeadersBodyToValidate<P, H, V> forValidation(C context) {
        return new PathHeadersBodyToValidate<>(
                toPath.map(context),
                toHeaders.map(context),
                toValidatableBody.map(context)
        );
    }

    /**
     * Converts context object into object that can be used for ASPSP API call.
     * @param context Context to convert
     * @return Object that can be used with {@code Xs2aAdapter} to perform ASPSP API calls
     */
    public ValidatedPathHeadersBody<P, H, B> forExecution(C context) {
        return new ValidatedPathHeadersBody<>(
                toPath.map(context),
                toHeaders.map(context),
                toBody.map(toValidatableBody.map(context))
        );
    }
}
