package de.adorsys.opba.fintech.impl.config;

import java.net.URI;
import java.util.List;

public interface Oauth2Config {
    String getClientId();
    String getClientSecret();
    URI getAuthenticationEndpoint();
    URI getCodeToTokenEndpoint();
    List<String> getScope();
    List<String> getAllowedEmailsRegex();
}
