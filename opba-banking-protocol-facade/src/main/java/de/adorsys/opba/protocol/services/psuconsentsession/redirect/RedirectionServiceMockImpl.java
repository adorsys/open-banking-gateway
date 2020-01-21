package de.adorsys.opba.protocol.services.psuconsentsession.redirect;

import de.adorsys.opba.protocol.services.psuconsentsession.PsuConsentSession;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class RedirectionServiceMockImpl implements RedirectionService {
    @Override
    public RedirectionToConsentAuthApi redirectForAuthorisation(PsuConsentSession psuConsentSession) {
        return new RedirectionToConsentAuthApi(URI.create("/path/resource?parameter=value"));
    }
}
