package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.FIN_TECH_AUTH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.TPP_AUTH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.SERVICE_SESSION_ID;
import static org.springframework.http.HttpStatus.ACCEPTED;

@Slf4j
public class HandleAcceptedService {
    private final AuthorizeService authorizeService;

    public HandleAcceptedService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    ResponseEntity handleAccepted(SessionEntity sessionEntity, HttpHeaders headers) {
        String authID = headers.getFirst(TPP_AUTH_ID);
        String serviceSessionID = headers.getFirst(SERVICE_SESSION_ID);
        URI location = headers.getLocation();
        log.info("call was accepted, but redirect has to be done for authID:{} location:{}",
                authID,
                location);

        updateSessionWithServiceSession(sessionEntity, serviceSessionID, authID);

        return ResponseEntity.status(ACCEPTED)
                .header(FIN_TECH_AUTH_ID, authID)
                .location(location)
                .build();
    }

    private void updateSessionWithServiceSession(SessionEntity sessionEntity, String serviceSessionIdString, String authID) {
        UUID serviceSessionId = StringUtils.isBlank(serviceSessionIdString)
                                        ? null
                                        : UUID.fromString(serviceSessionIdString);

        sessionEntity.setServiceSessionId(serviceSessionId);
        sessionEntity.setAuthId(authID);
        authorizeService.updateUserSession(sessionEntity);

    }
}
