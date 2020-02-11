package de.adorsys.opba.fintech.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.PSU_CONSENT_SESSION;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.REDIRECT_CODE;
import static org.springframework.http.HttpStatus.FOUND;

@Slf4j
public class HandleAcceptedService {
    ResponseEntity handleAccepted(HttpHeaders headers) {
        String authSessionID = headers.getFirst(AUTHORIZATION_SESSION_ID);
        String redirectCode = headers.getFirst(REDIRECT_CODE);
        String psuConsentSession = headers.getFirst(PSU_CONSENT_SESSION);
        URI location = headers.getLocation();
        log.info("call was accepted, but redirect has to be done for authSessionID:{} redirectCode:{} psuConsentSession:{} location:{}",
                authSessionID,
                redirectCode,
                psuConsentSession,
                location);
        return ResponseEntity.status(FOUND)
                .header(AUTHORIZATION_SESSION_ID, authSessionID)
                .header(REDIRECT_CODE, redirectCode)
                .header(PSU_CONSENT_SESSION, psuConsentSession)
                .location(location)
                .build();
    }
}
