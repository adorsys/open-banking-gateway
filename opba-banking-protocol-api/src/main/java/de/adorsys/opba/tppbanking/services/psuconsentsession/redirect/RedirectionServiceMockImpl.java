package de.adorsys.opba.tppbanking.services.psuconsentsession.redirect;

import org.springframework.stereotype.Service;

@Service
public class RedirectionServiceMockImpl implements RedirectionService {
    @Override
    public RedirectionToConsentAuthApi redirectForAuthorisation() {
        return new RedirectionToConsentAuthApi(new RedirectionToConsentAuthApi.Web("https://www.example.com/path/resource?parameter=value"));
    }
}
