package de.adorsys.opba.core.protocol.service.mapper;

import de.adorsys.opba.core.protocol.service.dto.PathHeadersBodyToValidate;
import de.adorsys.opba.core.protocol.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathHeadersBodyMapperTemplate<C extends BaseContext, P, H, V, B> {

    private final DtoMapper<C, V> toValidatableBody;
    private final DtoMapper<V, B> toBody;
    private final DtoMapper<C, H> toHeaders;
    private final DtoMapper<C, P> toPath;

    public PathHeadersBodyToValidate<P, H, V> forValidation(C context) {
        return new PathHeadersBodyToValidate<>(
                toPath.map(context),
                toHeaders.map(context),
                toValidatableBody.map(context)
        );
    }

    public ValidatedPathHeadersBody<P, H, B> forExecution(C context) {
        return new ValidatedPathHeadersBody<>(
                toPath.map(context),
                toHeaders.map(context),
                toBody.map(toValidatableBody.map(context))
        );
    }
}
