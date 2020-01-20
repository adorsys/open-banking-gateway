package de.adorsys.opba.tppbanking.services.psuconsentsession.redirect;

import lombok.Value;

@Value
public class RedirectionToConsentAuthApi {
    private final RedirectionToConsentAuthApi.Web web;

    @Value
    public static class Web {
        private final String href;
    }
}
