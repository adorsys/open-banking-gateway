package de.adorsys.opba.core.protocol.service.mapper;

import de.adorsys.opba.core.protocol.service.dto.PathHeadersToValidate;
import de.adorsys.opba.core.protocol.service.dto.ValidatedPathHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathHeadersMapperTemplate<C extends BaseContext, P, H> {

    private final DtoMapper<C, H> toHeaders;
    private final DtoMapper<C, P> toPath;

    public PathHeadersToValidate<P, H> forValidation(C context) {
        return new PathHeadersToValidate<>(
                toPath.map(context),
                toHeaders.map(context)
        );
    }

    public ValidatedPathHeaders<P, H> forExecution(C context) {
        return new ValidatedPathHeaders<>(
                toPath.map(context),
                toHeaders.map(context)
        );
    }
}
