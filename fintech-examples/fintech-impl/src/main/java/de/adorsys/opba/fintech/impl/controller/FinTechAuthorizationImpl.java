package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse200;
import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAuthorizationApi;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ContextInformation;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
            ArrayList<String> cookies = sessionEntity
                    .getCookies()
                    .stream()
                    .map(cookie ->
                            ResponseCookie.from(cookie.getName(), cookie.getValue())
                                    .httpOnly(cookieConfigProperties.isHttpOnly())
                                    .sameSite(cookieConfigProperties.getSameSite())
                                    .secure(cookieConfigProperties.isSecure())
                                    .path(cookieConfigProperties.getPath())
                                    .maxAge(cookieConfigProperties.getMaxAge())
                                    .build().toString())
                    .collect(Collectors.toCollection(ArrayList::new));
            responseHeaders.addAll(HttpHeaders.SET_COOKIE, cookies);

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
