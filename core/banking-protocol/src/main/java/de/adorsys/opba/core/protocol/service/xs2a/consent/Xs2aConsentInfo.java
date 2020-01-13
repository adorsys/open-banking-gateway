package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.core.protocol.domain.Approach.EMBEDDED;
import static de.adorsys.opba.core.protocol.domain.Approach.REDIRECT;

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
        return "finalised".equalsIgnoreCase(ctx.getScaStatus());
    }
}
