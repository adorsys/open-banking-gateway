package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateV139Headers;
import de.adorsys.xs2a.adapter.api.model.Consents;
import org.springframework.stereotype.Service;

/**
 * Maps the {@link de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent} from the context to XS2A adapter
 * usable request parameters.
 */
@Service
public class AisConsentInitiateV139Extractor extends PathHeadersBodyMapperTemplate<
    Xs2aAisContext,
    ConsentInitiateParameters,
        ConsentInitiateV139Headers,
    AisConsentInitiateBody,
    Consents> {

    public AisConsentInitiateV139Extractor(
            DtoMapper<Xs2aAisContext, AisConsentInitiateBody> toValidatableBody,
            DtoMapper<AisConsentInitiateBody, Consents> toBody,
            DtoMapper<Xs2aAisContext, ConsentInitiateV139Headers> toHeaders,
            DtoMapper<Xs2aAisContext, ConsentInitiateParameters> toParameters) {
        super(toValidatableBody, toBody, toHeaders, toParameters);
    }
}
