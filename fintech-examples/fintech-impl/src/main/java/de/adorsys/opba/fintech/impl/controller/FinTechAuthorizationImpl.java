package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse200;
import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.database.entities.CookieEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ContextInformation;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final CookieConfigProperties cookieConfigProperties;
    private final RedirectHandlerService redirectHandlerService;

    @Override
    public ResponseEntity<InlineResponse200> loginPOST(LoginRequest loginRequest, UUID xRequestID) {
        ContextInformation contextInformation = new ContextInformation(xRequestID);
        log.info("loginPost is called");
        Optional<SessionEntity> optionalUserEntity = authorizeService.login(loginRequest);
        if (optionalUserEntity.isPresent()) {
            SessionEntity sessionEntity = optionalUserEntity.get();

            InlineResponse200 response = new InlineResponse200();
            UserProfile userProfile = new UserProfile();
            userProfile.setName(sessionEntity.getLoginUserName());
            if (!sessionEntity.getLogins().isEmpty()) {
                userProfile.setLastLogin(sessionEntity.getLastLogin());
            }
            response.setUserProfile(userProfile);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(X_REQUEST_ID, contextInformation.getXRequestID().toString());
            log.info("set response cookie attributes to {}", cookieConfigProperties.toString());

            CookieEntity sessionCookie = sessionEntity.getSessionCookie();
            String sessionCookieString = ResponseCookie.from(sessionCookie.getName(), sessionCookie.getValue())
                    .httpOnly(cookieConfigProperties.getSessioncookie().isHttpOnly())
                    .sameSite(cookieConfigProperties.getSessioncookie().getSameSite())
                    .secure(cookieConfigProperties.getSessioncookie().isSecure())
                    .path(cookieConfigProperties.getSessioncookie().getPath())
                    .maxAge(cookieConfigProperties.getSessioncookie().getMaxAge())
                    .build().toString();
            responseHeaders.add(HttpHeaders.SET_COOKIE, sessionCookieString);
            responseHeaders.add(Consts.HEADER_XSRF_TOKEN, sessionEntity.getXsrfToken());

            return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    // TODO NOT WORKING YET
    @Override
    public ResponseEntity<Void> fromConsentOkGET(String authId, String redirectCode, UUID xRequestID, String xsrftoken) {
        return redirectHandlerService.doRedirect("redirectState", authId, redirectCode);
    }

    @Override
    public ResponseEntity<Void> logoutPOST(UUID xRequestID, String xsrfToken) {
        ContextInformation contextInformation = new ContextInformation(xRequestID);
        log.info("logoutPost is called");

        if (!authorizeService.isAuthorized(xsrfToken, null)) {
            log.warn("Request failed: Xsrf Token is wrong or user is not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        authorizeService.logout(xsrfToken, null);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_REQUEST_ID, contextInformation.getXRequestID().toString());
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
    }

}
