package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.Oauth2Provider;

import java.net.URI;
import java.util.Optional;

public interface Oauth2Authenticator {

    URI authenticateByRedirectingUserToIdp();
    Optional<String> authenticatedUserName(String code);
    Oauth2Provider getProvider();
}
