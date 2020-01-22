package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.service.mapper.HeadersBodyMapperTemplate;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.ConsentInitiateBody;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.xs2a.adapter.service.model.Consents;
import org.springframework.stereotype.Service;

@Service
public class ConsentInitiateExtractor extends HeadersBodyMapperTemplate<
        Xs2aContext,
        ConsentInitiateHeaders,
        ConsentInitiateBody,
        Consents> {

    public ConsentInitiateExtractor(
            DtoMapper<Xs2aContext, ConsentInitiateBody> toValidatableBody,
            DtoMapper<ConsentInitiateBody, Consents> toBody,
            DtoMapper<Xs2aContext, ConsentInitiateHeaders> toHeaders) {
        super(toValidatableBody, toBody, toHeaders);
    }
}
