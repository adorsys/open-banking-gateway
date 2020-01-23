package de.adorsys.opba.core.protocol.service.mapper;

import de.adorsys.opba.core.protocol.service.dto.ParametersHeadersToValidate;
import de.adorsys.opba.core.protocol.service.dto.ValidatedParametersHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParamsHeadersMapperTemplate<C extends BaseContext, H, P> {

    private final DtoMapper<C, H> toHeaders;
    private final DtoMapper<C, P> toParameters;

    public ParametersHeadersToValidate<P, H> forValidation(C context) {
        return new ParametersHeadersToValidate<>(
                toParameters.map(context),
                toHeaders.map(context)
        );
    }

    public ValidatedParametersHeaders<P, H> forExecution(C context) {
        return new ValidatedParametersHeaders<>(
                toParameters.map(context),
                toHeaders.map(context)
        );
    }
}
