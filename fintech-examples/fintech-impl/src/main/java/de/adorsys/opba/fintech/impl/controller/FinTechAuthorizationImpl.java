package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse200;
import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ConsentService;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSession;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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

    @Override
    public ResponseEntity<InlineResponse200> loginPOST(LoginRequest loginRequest, UUID xRequestID) {
        log.debug("loginPost is called for {}", loginRequest.getUsername());
        String xsrfToken = UUID.randomUUID().toString();
        Optional<SessionEntity> optionalUserEntity = authorizeService.login(loginRequest, xsrfToken);
        if (optionalUserEntity.isPresent()) {
            SessionEntity sessionEntity = optionalUserEntity.get();

            InlineResponse200 response = new InlineResponse200();
            UserProfile userProfile = new UserProfile();
            userProfile.setName(sessionEntity.getLoginUserName());
            if (!sessionEntity.getLogins().isEmpty()) {
                userProfile.setLastLogin(sessionEntity.getLastLogin());
            }
            response.setUserProfile(userProfile);

            HttpHeaders responseHeaders = authorizeService.modifySessionEntityAndCreateNewAuthHeader(restRequestContext.getRequestId(), optionalUserEntity.get(),
                    xsrfToken, cookieConfigProperties, SessionCookieType.REGULAR);
            return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity fromConsentOkGET(String authId, String finTechRedirectCode, UUID xRequestID, String xsrftoken) {
        PsuConsentSessionResponse response = consentService.confirmConsent(authId, xRequestID, finTechRedirectCode);
        PsuConsentSession psuConsentSession = response.getPsuConsentSession();
        if ("Confirmed".equals(psuConsentSession.getValue())) {
            authorizeService.getSession().setConsentConfirmed(true);
        }
        log.info(psuConsentSession.toString());

        return redirectHandlerService.doRedirect(authId, finTechRedirectCode);
    }

    @Override
    public ResponseEntity<Void> logoutPOST(UUID xRequestID, String xsrfToken) {
        log.info("logoutPost is called");

        if (!authorizeService.isAuthorized()) {
            log.warn("Request failed: Xsrf Token is wrong or user is not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        authorizeService.logout();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_REQUEST_ID, restRequestContext.getRequestId());
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
    }

}
