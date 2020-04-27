package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.tppclients.SessionCookieType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.FIN_TECH_AUTH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.FIN_TECH_REDIRECT_CODE;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.SERVICE_SESSION_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.TPP_AUTH_ID;
import static org.springframework.http.HttpStatus.ACCEPTED;

@Slf4j
public class HandleAcceptedService {
    private final AuthorizeService authorizeService;
    private final CookieConfigProperties cookieConfigProperties;
    private final RestRequestContext restRequestContext;


    public HandleAcceptedService(AuthorizeService authorizeService, CookieConfigProperties cookieConfigProperties, RestRequestContext restRequestContext) {
        this.authorizeService = authorizeService;
        this.cookieConfigProperties = cookieConfigProperties;
        this.restRequestContext = restRequestContext;
    }


    ResponseEntity handleAccepted(String fintechRedirectCode, SessionEntity sessionEntity, HttpHeaders headers) {
        sessionEntity.setAuthId(headers.getFirst(TPP_AUTH_ID));
        sessionEntity.setServiceSessionId(StringUtils.isBlank(headers.getFirst(SERVICE_SESSION_ID)) ? null : UUID.fromString(headers.getFirst(SERVICE_SESSION_ID)));

        URI location = headers.getLocation();
        log.info("call was accepted, but redirect has to be done for authID:{} location:{}", sessionEntity.getAuthId(), location);

        String xsrfToken = UUID.randomUUID().toString();
        HttpHeaders responseHeaders = authorizeService.modifySessionEntityAndCreateNewAuthHeader(restRequestContext.getRequestId(), sessionEntity,
                xsrfToken, cookieConfigProperties, SessionCookieType.REDIRECT);
        responseHeaders.add(FIN_TECH_AUTH_ID, sessionEntity.getAuthId());
        responseHeaders.add(FIN_TECH_REDIRECT_CODE, fintechRedirectCode);
        responseHeaders.setLocation(location);

        authorizeService.updateUserSession(sessionEntity);
        return new ResponseEntity<>(null, responseHeaders, ACCEPTED);
    }
}
