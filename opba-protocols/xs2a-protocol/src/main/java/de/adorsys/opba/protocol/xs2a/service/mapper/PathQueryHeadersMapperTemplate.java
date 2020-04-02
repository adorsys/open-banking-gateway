package de.adorsys.opba.protocol.xs2a.service.mapper;

import de.adorsys.opba.protocol.xs2a.service.dto.PathQueryHeadersToValidate;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathQueryHeadersMapperTemplate<C extends BaseContext, P, Q, H> {

    private final DtoMapper<? super C, H> toHeaders;
    private final DtoMapper<? super C, P> toPath;
    private final DtoMapper<? super C, Q> toQuery;

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
