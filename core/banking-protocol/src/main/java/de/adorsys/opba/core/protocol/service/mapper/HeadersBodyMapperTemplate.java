package de.adorsys.opba.core.protocol.service.mapper;

import de.adorsys.opba.core.protocol.service.dto.HeadersBodyToValidate;
import de.adorsys.opba.core.protocol.service.dto.ValidatedHeadersBody;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HeadersBodyMapperTemplate<C extends BaseContext, H, V, B> {

    private final DtoMapper<? super C, V> toValidatableBody;
    private final DtoMapper<V, B> toBody;
    private final DtoMapper<? super C, H> toHeaders;

    public HeadersBodyToValidate<H, V> forValidation(C context) {
        return new HeadersBodyToValidate<>(
                toHeaders.map(context),
                toValidatableBody.map(context)
        );
    }

    public ValidatedHeadersBody<H, B> forExecution(C context) {
        return new ValidatedHeadersBody<>(
                toHeaders.map(context),
                toBody.map(toValidatableBody.map(context))
        );
    }
}
