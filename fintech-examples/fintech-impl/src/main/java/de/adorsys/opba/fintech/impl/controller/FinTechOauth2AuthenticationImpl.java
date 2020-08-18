package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.resource.generated.FinTechOauth2AuthenticationApi;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.service.oauth2.Oauth2AuthResult;
import de.adorsys.opba.fintech.impl.service.oauth2.Oauth2Authenticator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.service.oauth2.Oauth2Const.COOKIE_OAUTH2_COOKIE_NAME;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinTechOauth2AuthenticationImpl implements FinTechOauth2AuthenticationApi {

    private final CookieConfigProperties properties;
    private final Set<Oauth2Authenticator> authenticators;

    @Override
    public ResponseEntity<Void> oauthLoginPOST(UUID xRequestID, String idpProviderId) {
        Oauth2Authenticator authenticator = authenticators.stream()
                .filter(it -> it.getProvider().name().toLowerCase().equals(idpProviderId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No IDP provider handler: " + idpProviderId));

        Oauth2AuthResult result = authenticator.authenticateByRedirectingUserToIdp();

        return ResponseEntity.accepted()
                .header(SET_COOKIE, properties.getOauth2cookie().buildCookie(COOKIE_OAUTH2_COOKIE_NAME, result.getState()))
                .header("Location", result.getRedirectTo().toASCIIString())
                .build();
    }
}
