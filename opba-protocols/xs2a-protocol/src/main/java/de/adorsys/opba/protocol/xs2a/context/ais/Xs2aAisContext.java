package de.adorsys.opba.protocol.xs2a.context.ais;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * XS2A AIS (Account Information Services) context. Represents general knowledge about currently executed request,
 * for example, contains outcome results from previous requests as well as the user input.
 */
// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aAisContext extends Xs2aContext {

    /**
     * AIS consent scope body object - whether it is Dedicated consent, All accounts (Global) consent, etc.
     */
    private AisConsentInitiateBody aisConsent = new AisConsentInitiateBody(); // to avoid initialization in more-parameters
}
