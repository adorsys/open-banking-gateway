package de.adorsys.opba.core.protocol.service.mapper;

import de.adorsys.opba.core.protocol.service.dto.QueryHeadersToValidate;
import de.adorsys.opba.core.protocol.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryHeadersMapperTemplate<C extends BaseContext, Q, H> {

    private final DtoMapper<? super C, H> toHeaders;
    private final DtoMapper<? super C, Q> toQuery;

    public QueryHeadersToValidate<Q, H> forValidation(C context) {
        return new QueryHeadersToValidate<>(
                toQuery.map(context),
                toHeaders.map(context)
        );
    }

    public ValidatedQueryHeaders<Q, H> forExecution(C context) {
        return new ValidatedQueryHeaders<>(
                toQuery.map(context),
                toHeaders.map(context)
        );
    }
}
