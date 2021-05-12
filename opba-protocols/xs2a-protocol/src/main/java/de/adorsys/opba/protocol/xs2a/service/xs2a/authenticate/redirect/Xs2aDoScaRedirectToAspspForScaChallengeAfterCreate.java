package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.redirect;

import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

/**
 * Performs redirection to the ASPSP by sending him to the page with redirection button (to ASPSP) for the redirect approach.
 * But is specific as happens just after consent was created.
 */
@Service("xs2aDoScaRedirectToAspspForScaChallengeAfterCreate")
public class Xs2aDoScaRedirectToAspspForScaChallengeAfterCreate extends Xs2aDoScaRedirectToAspspForScaChallenge {

    public Xs2aDoScaRedirectToAspspForScaChallengeAfterCreate(ProtocolUrlsConfiguration urlsConfiguration, RuntimeService runtimeService, Xs2aRedirectExecutor redirectExecutor) {
        super(urlsConfiguration, runtimeService, redirectExecutor);
    }

    @Override
    protected String getRedirectToAspspUrl(Xs2aContext context) {
        return context.getConsentOrPayemntCreateLinks().get(SCA_REDIRECT).getHref();
    }
}
