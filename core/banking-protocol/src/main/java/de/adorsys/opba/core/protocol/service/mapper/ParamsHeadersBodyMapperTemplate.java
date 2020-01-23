package de.adorsys.opba.core.protocol.service.mapper;

import de.adorsys.opba.core.protocol.service.dto.ParametersHeadersBodyToValidate;
import de.adorsys.opba.core.protocol.service.dto.ValidatedParametersHeadersBody;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParamsHeadersBodyMapperTemplate<C extends BaseContext, H, P, V, B> {

    private final DtoMapper<C, V> toValidatableBody;
    private final DtoMapper<V, B> toBody;
    private final DtoMapper<C, H> toHeaders;
    private final DtoMapper<C, P> toParameters;

    public ParametersHeadersBodyToValidate<P, H, V> forValidation(C context) {
        return new ParametersHeadersBodyToValidate<>(
                toParameters.map(context),
                toHeaders.map(context),
                toValidatableBody.map(context)
        );
    }

    public ValidatedParametersHeadersBody<P, H, B> forExecution(C context) {
        return new ValidatedParametersHeadersBody<>(
                toParameters.map(context),
                toHeaders.map(context),
                toBody.map(toValidatableBody.map(context))
        );
    }
}
