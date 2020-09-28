package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.xs2a.adapter.api.model.Consents;
import org.springframework.stereotype.Service;

/**
 * Maps the {@link de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent} from the context to XS2A adapter
 * usable request parameters.
 */
@Service
public class AisConsentInitiateExtractor extends PathHeadersBodyMapperTemplate<
    Xs2aAisContext,
    Xs2aAuthorizedConsentParameters,
    ConsentInitiateHeaders,
    AisConsentInitiateBody,
    Consents> {

    public AisConsentInitiateExtractor(
            DtoMapper<Xs2aAisContext, AisConsentInitiateBody> toValidatableBody,
            DtoMapper<AisConsentInitiateBody, Consents> toBody,
            DtoMapper<Xs2aAisContext, ConsentInitiateHeaders> toHeaders,
            DtoMapper<Xs2aAisContext, Xs2aAuthorizedConsentParameters> toParameters) {
        super(toValidatableBody, toBody, toHeaders, toParameters);
    }
}
