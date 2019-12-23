package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.gateway;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import org.springframework.stereotype.Service;

@Service("xs2aAspspMethodGateway")
public class AspspMethodGateway {

    public boolean isEmbedded(Xs2aContext ctx) {
        return "EMBEDDED".equalsIgnoreCase(ctx.getAspspScaApproach());
    }

    public boolean isRedirect(Xs2aContext ctx) {
        return "REDIRECT".equalsIgnoreCase(ctx.getAspspScaApproach());
    }
}
