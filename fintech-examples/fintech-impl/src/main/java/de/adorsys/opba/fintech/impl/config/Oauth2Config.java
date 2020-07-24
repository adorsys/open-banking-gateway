package de.adorsys.opba.fintech.impl.config;

public interface Oauth2Config {
    String getClientId();

    String getClientSecret();

    java.net.URI getAuthenticationEndpoint();

    java.net.URI getCodeToTokenEndpoint();

    java.util.List<String> getScope();

    java.util.List<String> getAllowedEmailsRegex();
}
