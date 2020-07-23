package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.resource.generated.FinTechOauth2AuthenticationApi;
import de.adorsys.opba.fintech.impl.service.Oauth2Authenticator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinTechOauth2AuthenticationImpl implements FinTechOauth2AuthenticationApi {

    private final Set<Oauth2Authenticator> authenticators;

    @Override
    public ResponseEntity<Void> oauthLoginPOST(UUID xRequestID, String idpProviderId) {
        Oauth2Authenticator authenticator = authenticators.stream()
                .filter(it -> it.getProvider().name().toLowerCase().equals(idpProviderId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No IDP provider handler: " + idpProviderId));

        URI redirectTo = authenticator.authenticateByRedirectingUserToIdp();

        return ResponseEntity.accepted()
                .header("Location", redirectTo.toASCIIString())
                .build();
    }
}
