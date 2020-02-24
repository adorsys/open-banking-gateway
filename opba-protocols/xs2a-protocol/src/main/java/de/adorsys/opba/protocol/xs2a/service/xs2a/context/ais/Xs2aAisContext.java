package de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import lombok.Data;
import lombok.EqualsAndHashCode;

// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aAisContext extends Xs2aContext {

    private AisConsentInitiateBody aisConsent = new AisConsentInitiateBody(); // to avoid initialization in more-parameters
}
