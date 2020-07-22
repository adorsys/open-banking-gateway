package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.resource.generated.FinTechGmailAuthenticationApi;
import de.adorsys.opba.fintech.impl.service.GmailOauth2AuthenticateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinTechGmailAuthenticationImpl implements FinTechGmailAuthenticationApi {

    private final GmailOauth2AuthenticateService oauth2AuthenticateService;

    @Override
    public ResponseEntity<Void> loginPOST(UUID xRequestID) {
        URI redirectTo = oauth2AuthenticateService.authenticateByRedirectingTo();

        return ResponseEntity.accepted()
                .header("Location", redirectTo.toASCIIString())
                .build();
    }
}
