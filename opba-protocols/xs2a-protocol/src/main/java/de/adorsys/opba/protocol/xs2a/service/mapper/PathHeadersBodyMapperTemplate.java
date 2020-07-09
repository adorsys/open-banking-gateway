package de.adorsys.opba.protocol.xs2a.service.mapper;

import de.adorsys.opba.protocol.xs2a.service.dto.PathHeadersBodyToValidate;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathHeadersBodyMapperTemplate<C extends BaseContext, P, H, V, B> {

    private final DtoMapper<? super C, V> toValidatableBody;
    private final DtoMapper<V, B> toBody;
    private final DtoMapper<? super C, H> toHeaders;
    private final DtoMapper<? super C, P> toPath;

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
