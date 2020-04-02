package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.PSU_CONSENT_SESSION;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.REDIRECT_CODE;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.SERVICE_SESSION_ID;
import static org.springframework.http.HttpStatus.ACCEPTED;

@Slf4j
public class HandleAcceptedService {
    private final AuthorizeService authorizeService;

    public HandleAcceptedService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    ResponseEntity handleAccepted(SessionEntity sessionEntity, HttpHeaders headers) {
        String authSessionID = headers.getFirst(AUTHORIZATION_SESSION_ID);
        String redirectCode = headers.getFirst(REDIRECT_CODE);
        String psuConsentSession = headers.getFirst(PSU_CONSENT_SESSION);
        String serviceSessionID = headers.getFirst(SERVICE_SESSION_ID);
        URI location = headers.getLocation();
        log.info("call was accepted, but redirect has to be done for authSessionID:{} redirectCode:{} psuConsentSession:{} location:{}",
                authSessionID,
                redirectCode,
                psuConsentSession,
                location);

        updateSessionWithServiceSession(sessionEntity, serviceSessionID);

        return ResponseEntity.status(ACCEPTED)
                .header(AUTHORIZATION_SESSION_ID, authSessionID)
                .header(REDIRECT_CODE, redirectCode)
                .header(PSU_CONSENT_SESSION, psuConsentSession)
                .header(SERVICE_SESSION_ID, serviceSessionID)
                .location(location)
                .build();
    }

    private void updateSessionWithServiceSession(SessionEntity sessionEntity, String serviceSessionIdString) {
        UUID serviceSessionId = StringUtils.isBlank(serviceSessionIdString)
                                        ? null
                                        : UUID.fromString(serviceSessionIdString);

        sessionEntity.setServiceSessionId(serviceSessionId);
        authorizeService.updateUserSession(sessionEntity);
    }
}
