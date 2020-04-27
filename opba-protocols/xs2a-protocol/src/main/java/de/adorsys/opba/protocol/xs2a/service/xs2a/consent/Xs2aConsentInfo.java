package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.api.common.Approach.EMBEDDED;
import static de.adorsys.opba.protocol.api.common.Approach.REDIRECT;
import static de.adorsys.opba.protocol.xs2a.service.xs2a.consent.ConsentConst.CONSENT_FINALIZED;

/**
 * Generic information service about the consent based on current context.
 */
@Service("xs2aConsentInfo")
public class Xs2aConsentInfo {

    /**
     * Is the current consent authorization in EMBEDDED mode.
     */
    public boolean isEmbedded(Xs2aContext ctx) {
        return EMBEDDED.name().equalsIgnoreCase(ctx.getAspspScaApproach());
    }

    /**
     * Is the current consent authorization in REDIRECT mode.
     */
    public boolean isRedirect(Xs2aContext ctx) {
        return REDIRECT.name().equalsIgnoreCase(ctx.getAspspScaApproach());
    }

    /**
     * Is the current consent authorization using multiple SCA methods (SMS,email,etc.)
     */
    public boolean isMultipleScaAvailable(Xs2aContext ctx) {
        return null != ctx.getAvailableSca() && !ctx.getAvailableSca().isEmpty();
    }

    /**
     * Is the PSU password present in the context.
     */
    public boolean isPasswordPresent(Xs2aContext ctx) {
        return null != ctx.getPsuPassword();
    }

    /**
     * Is the consent authorized and approved.
     */
    public boolean isConsentFinalized(Xs2aContext ctx) {
        return CONSENT_FINALIZED.equalsIgnoreCase(ctx.getScaStatus());
    }

    /**
     * Was the PSU password that was sent to ASPSP wrong.
     */
    public boolean isWrongPassword(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }

    /**
     * Was the SCA challenge result that was sent to ASPSP wrong.
     */
    public boolean isWrongScaChallenge(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }

    /**
     * Was the redirection from ASPSP in REDIRECT mode using OK (consent granted) or NOK url (consent denied).
     */
    public boolean isOkRedirectConsent(Xs2aContext ctx) {
        return ctx.isRedirectConsentOk();
    }

    /**
     * Generic wrong credentials indicator.
     */
    public boolean hasWrongCredentials(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }
}
