package de.adorsys.opba.core.protocol.service.mapper;

import de.adorsys.opba.core.protocol.service.dto.PathQueryHeadersToValidate;
import de.adorsys.opba.core.protocol.service.dto.ValidatedPathQueryHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathQueryHeadersMapperTemplate<C extends BaseContext, P, Q, H> {

    private final DtoMapper<C, H> toHeaders;
    private final DtoMapper<C, P> toPath;
    private final DtoMapper<C, Q> toQuery;

    public PathQueryHeadersToValidate<P, Q, H> forValidation(C context) {
        return new PathQueryHeadersToValidate<>(
                toPath.map(context),
                toQuery.map(context),
                toHeaders.map(context)
        );
    }

    public ValidatedPathQueryHeaders<P, Q, H> forExecution(C context) {
        return new ValidatedPathQueryHeaders<>(
                toPath.map(context),
                toQuery.map(context),
                toHeaders.map(context)
        );
    }
}
