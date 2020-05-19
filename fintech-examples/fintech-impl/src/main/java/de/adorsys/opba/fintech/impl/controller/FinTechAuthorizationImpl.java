package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse200;
import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.LoginEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.database.repositories.LoginRepository;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ConsentService;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
import de.adorsys.opba.fintech.impl.tppclients.SessionCookieType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.X_REQUEST_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinTechAuthorizationImpl implements FinTechAuthorizationApi {

    private final AuthorizeService authorizeService;
    private final RedirectHandlerService redirectHandlerService;
    private final RestRequestContext restRequestContext;
    private final ConsentService consentService;
    private final CookieConfigProperties cookieConfigProperties;
    private final LoginRepository loginRepository;
    private final ConsentRepository consentRepository;

    @Override
    public ResponseEntity<InlineResponse200> loginPOST(LoginRequest loginRequest, UUID xRequestID) {
        log.debug("loginPost is called for {}", loginRequest.getUsername());
        String xsrfToken = UUID.randomUUID().toString();
        Optional<SessionEntity> optionalSessionEntity = authorizeService.login(loginRequest, xsrfToken);
        if (!optionalSessionEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SessionEntity sessionEntity = optionalSessionEntity.get();
        InlineResponse200 response = new InlineResponse200();
        UserProfile userProfile = new UserProfile();
        userProfile.setName(sessionEntity.getUserEntity().getLoginUserName());
        List<LoginEntity> logins = new ArrayList<>();
        loginRepository.findByUserEntityOrderByLoginTimeDesc(sessionEntity.getUserEntity()).forEach(logins::add);
        if (logins.size() > 1) {
            userProfile.setLastLogin(logins.get(1).getLoginTime());
            log.info("last login was {}", userProfile.getLastLogin());
        } else {
            log.info("this was very first login");
        }
        response.setUserProfile(userProfile);

        HttpHeaders responseHeaders = authorizeService.modifySessionEntityAndCreateNewAuthHeader(restRequestContext.getRequestId(), optionalSessionEntity.get(),
                xsrfToken, cookieConfigProperties, SessionCookieType.REGULAR, null);
        return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> fromConsentGET(String authId, String okOrNotokString, String finTechRedirectCode, UUID xRequestID, String xsrfToken) {
        OkOrNotOk okOrNotOk = OkOrNotOk.valueOf(okOrNotokString);
        log.info("fromConsentGET path is \"/v1/{}/fromConsent/{}\"", authId, okOrNotOk);
        if (!authorizeService.isAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (okOrNotOk.equals(OkOrNotOk.OK) && consentService.confirmConsent(authId, xRequestID)) {

            Optional<ConsentEntity> consent = consentRepository.findByAuthId(authId);

            if (!consent.isPresent()) {
                throw new RuntimeException("consent for authid " + authId + " can not be found");
            }

            log.info("consent with authId {} is now valid", authId);
            consent.get().setConsentConfirmed(true);
            consentRepository.save(consent.get());
        }
        return redirectHandlerService.doRedirect(authId, finTechRedirectCode, okOrNotOk);
    }

    @Override
    public ResponseEntity<Void> logoutPOST(UUID xRequestID, String xsrfToken) {
        log.info("logoutPost is called");

        if (!authorizeService.isAuthorized()) {
            log.warn("logoutPOST failed: user is not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        authorizeService.logout();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_REQUEST_ID, restRequestContext.getRequestId());
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
    }

}
