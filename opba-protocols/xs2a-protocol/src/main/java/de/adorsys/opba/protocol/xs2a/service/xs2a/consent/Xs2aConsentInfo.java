package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.api.common.Approach.EMBEDDED;
import static de.adorsys.opba.protocol.api.common.Approach.REDIRECT;
import static de.adorsys.opba.protocol.xs2a.service.xs2a.consent.ConsentConst.CONSENT_FINALIZED;

@Service("xs2aConsentInfo")
public class Xs2aConsentInfo {

    public boolean isEmbedded(Xs2aContext ctx) {
        return EMBEDDED.name().equalsIgnoreCase(ctx.getAspspScaApproach());
    }

    public boolean isRedirect(Xs2aContext ctx) {
        return REDIRECT.name().equalsIgnoreCase(ctx.getAspspScaApproach());
    }

    public boolean isMultipleScaAvailable(Xs2aContext ctx) {
        return null != ctx.getAvailableSca() && !ctx.getAvailableSca().isEmpty();
    }

    public boolean isPasswordPresent(Xs2aContext ctx) {
        return null != ctx.getPsuPassword();
    }

    public boolean isConsentFinalized(Xs2aContext ctx) {
        return CONSENT_FINALIZED.equalsIgnoreCase(ctx.getScaStatus());
    }

    public boolean isWrongPassword(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }

    public boolean isWrongScaChallenge(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }

    public boolean isOkRedirectConsent(Xs2aContext ctx) {
        return ctx.isRedirectConsentOk();
    }

    public boolean hasWrongCredentials(Xs2aContext ctx) {
        return null != ctx.getWrongAuthCredentials() && ctx.getWrongAuthCredentials();
    }
}
