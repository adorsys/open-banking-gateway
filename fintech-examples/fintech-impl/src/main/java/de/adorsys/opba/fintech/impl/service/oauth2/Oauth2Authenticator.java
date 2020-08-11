package de.adorsys.opba.fintech.impl.service.oauth2;

import de.adorsys.opba.fintech.impl.config.Oauth2Provider;

import java.util.Optional;

public interface Oauth2Authenticator {

    Oauth2AuthResult authenticateByRedirectingUserToIdp();
    Optional<String> authenticatedUserName(String code);
    Oauth2Provider getProvider();
}
