package de.adorsys.opba.protocol.services.psuconsentsession.redirect;

import lombok.Value;

import java.net.URI;

@Value
public class RedirectionToConsentAuthApi {
    private final URI redirectionUrl;
}
