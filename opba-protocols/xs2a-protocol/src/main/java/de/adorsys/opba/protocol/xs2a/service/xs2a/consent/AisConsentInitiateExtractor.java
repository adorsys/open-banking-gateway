package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.mapper.HeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.xs2a.adapter.service.model.Consents;
import org.springframework.stereotype.Service;

/**
 * Maps the {@link de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent} from the context to XS2A adapter
 * usable request parameters.
 */
@Service
public class AisConsentInitiateExtractor extends HeadersBodyMapperTemplate<
    Xs2aAisContext,
    ConsentInitiateHeaders,
    AisConsentInitiateBody,
    Consents> {

    public AisConsentInitiateExtractor(
            DtoMapper<Xs2aAisContext, AisConsentInitiateBody> toValidatableBody,
            DtoMapper<AisConsentInitiateBody, Consents> toBody,
            DtoMapper<Xs2aAisContext, ConsentInitiateHeaders> toHeaders) {
        super(toValidatableBody, toBody, toHeaders);
    }
}
