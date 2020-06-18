package de.adorsys.opba.protocol.xs2a.service.mapper;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.dto.HeadersBodyToValidate;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedHeadersBody;
import lombok.RequiredArgsConstructor;

/**
 * Mapper class to map from context object like {@link de.adorsys.opba.protocol.xs2a.context.Xs2aContext}
 * to ASPSP API request parameters (headers and body).
 * @param <C> Context class
 * @param <H> ASPSP API headers class
 * @param <V> ASPSP API object to validate after mapping, compatible with {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator}
 * @param <B> ASPSP API object to use when doing API call
 */
@RequiredArgsConstructor
public class HeadersBodyMapperTemplate<C extends BaseContext, H, V, B> {

    private final DtoMapper<? super C, V> toValidatableBody;
    private final DtoMapper<V, B> toBody;
    private final DtoMapper<? super C, H> toHeaders;

    /**
     * Converts context object into object that can be used for validation.
     * @param context Context to convert
     * @return Validatable object that can be used with {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator}
     * to check if all necessary parameters are present
     */
    public HeadersBodyToValidate<H, V> forValidation(C context) {
        return new HeadersBodyToValidate<>(
                toHeaders.map(context),
                toValidatableBody.map(context)
        );
    }

    /**
     * Converts context object into object that can be used for ASPSP API call.
     * @param context Context to convert
     * @return Object that can be used with {@code Xs2aAdapter} to perform ASPSP API calls
     */
    public ValidatedHeadersBody<H, B> forExecution(C context) {
        return new ValidatedHeadersBody<>(
                toHeaders.map(context),
                toBody.map(toValidatableBody.map(context))
        );
    }
}
